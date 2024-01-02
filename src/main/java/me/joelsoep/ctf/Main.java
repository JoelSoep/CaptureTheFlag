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
import com.fren_gor.ultimateAdvancementAPI.AdvancementMain;
import com.mongodb.ConnectionString;
import me.joelsoep.ctf.autoBroadcast.BroadcastService;
import me.joelsoep.ctf.command.DebugCommand;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameQueue;
import me.joelsoep.ctf.game.advancement.AdvancementAPI;
import me.joelsoep.ctf.game.advancement.AdvancementService;
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
    private AdvancementMain advancementMain;

    public static Game game = new Game();
    public static final MongoManager mongoManager = new MongoManager(new ConnectionString("mongodb+srv://admin:uFrJ2m8zDDhALE9i@ctf.bh3mqpu.mongodb.net/?retryWrites=true&w=majority"));
    public static final KitService kitService = new KitService();
    public static final SpecialService specialService = new SpecialService();
    public static final GameQueue queue = new GameQueue();
    public static CTFLogger logger = null;
    public static AdvancementAPI advancementAPI;
    public static AdvancementService advancementService;

    private static Main instance;

    @Override
    public void onLoad() {
       // this.advancementMain = new AdvancementMain(this);
       // this.advancementMain.load();
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
       // this.advancementMain.enableMySQL(
        //        "network",
        //        "admin",
         //       "ctf",
         //       "23.88.124.188",
         //       3306,
          //      10,
          //      6000
      //  );
         //   LogModule.CORE.log("Booting CTF-Advancements...");
         //   Main.advancementAPI = new AdvancementAPI(this);
         //   Main.advancementService = new AdvancementService();
        Main.specialService.loadSpecials("me.joelsoep.ctf.game.special.specials", this);
        new MapManager();
        new BroadcastService(this).runAll();
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new DebugCommand(this));
                // Plugin startup logic
    }

    @Override
    public void onDisable() {
     //   Main.mongo.getConfigCollection().findOneAndUpdate(Filters.eq("_id", 0), new Document("gameCount", Main.mongo.getGameCount()));
        //Plugin shutdown logic
       // this.advancementMain.disable();
    }

    public static Main getInstance() {
        return instance;
    }
}
