/**
 * © 2023 Hendrik Joël Soepenberg. All rights reserved.
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
import me.joelsoep.ctf.command.DebugCommand;
import me.joelsoep.ctf.command.SpectateCommand;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameQueue;
import me.joelsoep.ctf.game.kit.KitService;
import me.joelsoep.ctf.game.special.SpecialService;
import me.joelsoep.ctf.game.task.BackupPlayerDataTask;
import me.joelsoep.ctf.game.task.QueueCheckGameAvailabilityTask;
import me.joelsoep.ctf.game.task.QueuePlayersActionbarTask;
import me.joelsoep.ctf.listener.ListenerService;
import me.joelsoep.ctf.logger.CTFLogger;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Main extends JavaPlugin {

    @Getter private static final Game game = new Game();
    @Getter private static final MongoManager mongoManager = new MongoManager(new ConnectionString("mongodb+srv://admin:uFrJ2m8zDDhALE9i@ctf.bh3mqpu.mongodb.net/?retryWrites=true&w=majority"));
    @Getter private static final KitService kitService = new KitService();
    @Getter private static final SpecialService specialService = new SpecialService();
    @Getter private static final GameQueue queue = new GameQueue();
    @Getter private static CTFLogger logger = null;

    private static Main instance;

    @Override
    public void onLoad() {
        instance = this;
        logger = new CTFLogger();
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

        new ListenerService(this);
        new BackupPlayerDataTask(this);
        new QueuePlayersActionbarTask(this);
        new QueueCheckGameAvailabilityTask(this);

        Main.getSpecialService().loadSpecials("me.joelsoep.ctf.game.special.specials", this);
        new MapManager();
        new BroadcastService(this).runAll();
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new DebugCommand(this));
        manager.registerCommand(new SpectateCommand());
                // Plugin startup logic
    }

    @Override
    public void onDisable() {
        Main.logger.closeFileHandler();
    }

    public static Main getInstance() {
        return instance;
    }
}
