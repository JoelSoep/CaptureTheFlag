/**
 * © 2023-2025 Hendrik Joël Soepenberg. All rights reserved.
 * <p>
 * This software is proprietary and confidential.
 * Unauthorized copying, modification, or distribution of this software,
 * or any portion of it, is strictly prohibited and may result in legal action.
 * <p>
 * For licensing information and inquiries, please visit: https://www.soep.me
 */

package me.joelsoep.ctf;

import co.aikar.commands.PaperCommandManager;
import com.mongodb.ConnectionString;
import fr.skytasul.glowingentities.GlowingEntities;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.autoBroadcast.BroadcastService;
import me.joelsoep.ctf.command.CTFCommand;
import me.joelsoep.ctf.command.CoinsCommand;
import me.joelsoep.ctf.command.DebugCommand;
import me.joelsoep.ctf.command.SpectateCommand;
import me.joelsoep.ctf.cosmetic.CosmeticsManager;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.discord.DiscordWebhook;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameQueue;
import me.joelsoep.ctf.game.kit.KitService;
import me.joelsoep.ctf.game.special.SpecialService;
import me.joelsoep.ctf.game.task.*;
import me.joelsoep.ctf.listener.ListenerService;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.ServerMode;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private static final boolean preloadClasses = preloadClasses();
    @Getter private static final Game game = new Game();
    @Getter @Nullable private static CTFServer gameServer = null;
    @Getter private static KitService kitService = null;
    @Getter private static SpecialService specialService = null;
    @Getter private static final GameQueue gameQueue = new GameQueue();
    @Getter private static CosmeticsManager cosmeticsManager;
    @Getter private static Main instance = null;
    @Getter private static BackupPlayerDataTask backupTask = null;
    @Getter private static GlowingEntities glowingEntitiesAPI = null;
    @Getter @Setter private static ServerMode mode = null;
    @Getter private static InstanceSettings settings = new InstanceSettings();
    @Getter @Setter private static RegisteredListener mcmhChatListener = null;

    @Override
    public void onLoad() {
        instance = this;

        // for whatever reason, if we don't preload several packets like the packet builder,
        // it will unload the packet builder while disabling before we have actually sent a goodbye packet.
        // preloading it and pinning the inner class in JVM's memory bypasses this early unload.
        preloadClasses();

        String mongodb_uri = getConfig().getString("mongodb-uri");

        if (mongodb_uri == null) {
            LogModule.CORE.severe("""
                    The config.yml does not include a valid MongoDB connection string.
                    Aborting.""");
            Bukkit.getServer().shutdown();
            return;
        }
        new MongoManager(new ConnectionString(mongodb_uri));
        LogModule.DATABASE.log("Connected to MDB!");

        setMode(LocalConfig.MODE.equalsIgnoreCase("tournament") ? ServerMode.TOURNAMENT : ServerMode.NORMAL);

        if (LocalConfig.DISCORD_WEBHOOK_URL == null || LocalConfig.DISCORD_WEBHOOK_URL.isBlank()) {
            LogModule.CORE.severe("The configuration files do not contain a valid webhook URL.");
            LogModule.CORE.severe("disabling.");
            Bukkit.getServer().shutdown();
        }

        if (getMode() == ServerMode.TOURNAMENT && !LocalConfig.PROXY_MODE) {
            LogModule.CORE.severe("Found TOURNAMENT mode in config, but proxy mode is disabled!!");
            LogModule.CORE.severe("Continuing in NORMAL mode.");
            setMode(ServerMode.NORMAL);
        }
        if (LocalConfig.PROXY_MODE) {
            loadServer();
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!Bukkit.getServer().getAllowFlight()) {
            LogModule.CORE.log(Level.WARNING, """
                    This server disallows player flight.
                    It is highly recommended to allow flight, as disabling this could cause issues.
                    Keeping allow flight disabled is at your own risk.
                    """);
        }

        backupTask = new BackupPlayerDataTask();
        cosmeticsManager = new CosmeticsManager();
        kitService = new KitService();
        specialService = new SpecialService();
        glowingEntitiesAPI = new GlowingEntities(this);
        new ListenerService("me.joelsoep.ctf.listener");
        new QueuePlayersActionbarTask();
        new QueueCheckGameAvailabilityTask();

        Main.getSpecialService().loadSpecials();
        DiscordWebhook.init();
        new StoreManager();
        new MapManager();
        new BroadcastService().runAll();
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new DebugCommand());
        manager.registerCommand(new SpectateCommand());
        manager.registerCommand(new CTFCommand());
        manager.registerCommand(new CoinsCommand());

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new CleanExpiredUltraShieldsTask(), 0L, 5L);
        Bukkit.getScheduler().runTaskAsynchronously(this, new ResumePendingTransactionsTask());

        // finish boot
        if (LocalConfig.PROXY_MODE) {
            // this should never be null
            getGameServer().hello();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.broadcast(MM.deserialize("BACKING UP ALL PLAYERS BEFORE SHUTDOWN"));
        Main.getBackupTask().backupAll();
        Bukkit.broadcast(MM.deserialize("DONE!"));

        if (LocalConfig.PROXY_MODE) {
            try {
                LogModule.CORE.log("BLOCKING MAIN THREAD UNTIL SERVER HAS UNREGISTERED");
                long ms = System.currentTimeMillis();
                gameServer.goodbye();
                gameServer.unregisterServer().get();
                long newMs = System.currentTimeMillis();
                int seconds = Math.toIntExact((newMs - ms) / 1000);
                LogModule.CORE.log("DONE! (in %s seconds)".formatted(seconds));
                LogModule.CORE.log("Unregistered server %s from the database.".formatted(FileManager.getServerConfig().get("id")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        glowingEntitiesAPI.disable();
        MongoManager.shutdown();
        LogModule.CORE.log("Shut down CTF.");
    }

    private static boolean preloadClasses() {
        try {
            Class.forName("me.joelsoep.ctf.server.packet.packets.GoodbyePacket$Builder");
            Class.forName("me.joelsoep.ctf.database.subscriber.UnregisterServerSubscriber");
            CTFServer.preloadShutdownModule();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Should never happen
            return false;
        }
    }

    public void loadServer() {
        LogModule.CORE.log("Booting ctf-server...");
        FileManager.saveServerConfig();

        if (LocalConfig.PROXY_MODE_HOST == null || LocalConfig.PROXY_MODE_ID == null || LocalConfig.PROXY_MODE_PORT == 0) {
            LogModule.CORE.severe("""
                    The server.yml does not include a valid server configuration.
                    Aborting.""");
            Bukkit.getServer().shutdown();
            return;
        }
        gameServer = new CTFServer(LocalConfig.PROXY_MODE_ID, LocalConfig.PROXY_MODE_HOST, LocalConfig.PROXY_MODE_PORT, getMode());
    }
}
