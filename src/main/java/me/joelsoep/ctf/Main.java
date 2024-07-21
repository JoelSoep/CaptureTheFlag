/**
 * © 2023-2024 Hendrik Joël Soepenberg. All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized copying, modification, or distribution of this software,
 * or any portion of it, is strictly prohibited and may result in legal action.
 *
 * For licensing information and inquiries, please visit: https://www.soep.me
 */

package me.joelsoep.ctf;

import co.aikar.commands.PaperCommandManager;
import com.mongodb.ConnectionString;
import lombok.Getter;
import me.joelsoep.ctf.autoBroadcast.BroadcastService;
import me.joelsoep.ctf.command.CTFCommand;
import me.joelsoep.ctf.command.DebugCommand;
import me.joelsoep.ctf.command.SpectateCommand;
import me.joelsoep.ctf.cosmetic.CosmeticManager;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameQueue;
import me.joelsoep.ctf.game.kit.KitService;
import me.joelsoep.ctf.game.special.SpecialService;
import me.joelsoep.ctf.game.task.BackupPlayerDataTask;
import me.joelsoep.ctf.game.task.QueueCheckGameAvailabilityTask;
import me.joelsoep.ctf.game.task.QueuePlayersActionbarTask;
import me.joelsoep.ctf.listener.ListenerService;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Main extends JavaPlugin {

    @Getter private static final Game game = new Game();
    @Getter private static MongoManager mongoManager = null;
    @Getter private static final KitService kitService = new KitService();
    @Getter private static final SpecialService specialService = new SpecialService();
    @Getter private static final GameQueue queue = new GameQueue();
    @Getter private static CosmeticManager cosmeticManager;
    @Getter private static Main instance = null;
    @Getter private static BackupPlayerDataTask backupTask = null;

    @Override
    public void onLoad() {
        instance = this;

        String mongodb_uri = getConfig().getString("mongodb-uri");

        if (mongodb_uri == null) {
            LogModule.CORE.severe("""
                    The config.yml does not include a valid MongoDB connection string.
                    Aborting.""");
            Bukkit.getServer().shutdown();
            return;
        }
        mongoManager = new MongoManager(new ConnectionString(mongodb_uri));
        LogModule.DATABASE.log("Connected to MDB!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!getServer().getAllowFlight()) {
           LogModule.CORE.log(Level.WARNING,"""
                    This server disallows player flight.
                    It is highly recommended to allow flight, as disabling this could cause issues.
                    Keeping allow flight disabled is at your own risk.
                    """);
        }

        backupTask = new BackupPlayerDataTask();
        cosmeticManager = new CosmeticManager();
        new ListenerService("me.joelsoep.ctf.listener");
        new QueuePlayersActionbarTask();
        new QueueCheckGameAvailabilityTask();

        Main.getSpecialService().loadSpecials();
        new MapManager();
        new BroadcastService().runAll();
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new DebugCommand());
        manager.registerCommand(new SpectateCommand());
        manager.registerCommand(new CTFCommand());
    }

    @Override
    public void onDisable() {
        Bukkit.broadcast(MM.deserialize("BACKING UP ALL PLAYERS BEFORE SHUTDOWN"));
        Main.getBackupTask().backupAll();
        Bukkit.broadcast(MM.deserialize("DONE!"));
    }
}
