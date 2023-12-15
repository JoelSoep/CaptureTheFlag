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
import me.joelsoep.ctf.command.DebugCommand;
import me.joelsoep.ctf.database.Mongo;
import me.joelsoep.ctf.game.GameQueue;
import me.joelsoep.ctf.game.advancement.AdvancementAPI;
import me.joelsoep.ctf.game.advancement.AdvancementService;
import me.joelsoep.ctf.game.special.SpecialService;
import me.joelsoep.ctf.game.task.BackupPlayerDataTask;
import me.joelsoep.ctf.game.task.CountdownTask;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.kit.KitService;
import me.joelsoep.ctf.game.task.QueueCheckGameAvailabilityTask;
import me.joelsoep.ctf.game.task.QueuePlayersActionbarTask;
import me.joelsoep.ctf.listener.*;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    private AdvancementMain advancementMain;

    public static LinkedHashMap<String, CTFPlayer> playerList = new LinkedHashMap<>();
    public static Game game = new Game();
    public static final Mongo mongo = new Mongo();
    public static final KitService kitService = new KitService();
    public static final SpecialService specialService = new SpecialService();
    public static final GameQueue queue = new GameQueue();
    public static CountdownTask countdownTask;
    public static AdvancementAPI advancementAPI;
    public static AdvancementService advancementService;

    @Override
    public void onLoad() {
      // this.advancementMain = new AdvancementMain(this);
      //  this.advancementMain.load();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!getServer().getAllowFlight()) {
            Bukkit.getLogger().warning("""
                    This server disallows player flight.
                    It is highly recommended to allow flight, as disabling this could cause issues.
                    Keeping allow flight disabled is at your own risk.
                    """);
        }

        new BackupPlayerDataTask(this);
        new QueuePlayersActionbarTask(this);
        new QueueCheckGameAvailabilityTask(this);
      //  this.advancementMain.enableMySQL(
       //         "network",
       //         "admin",
       //         "ctf",
       //         "23.88.124.188",
       //         3306,
       //         10,
       //         6000
       // );
          //  Bukkit.getLogger().log(Level.INFO, "Booting CTF-Advancements...");
          //  Main.advancementAPI = new AdvancementAPI(this);
           // Main.advancementService = new AdvancementService();

        //saveFile("schem.zip", "https://download1590.mediafire.com/ugi4gapkncfgbmlMtE7AGRiQSqqEvrIhGvDf_k3cT2WEa0_Mv_QrHjJhO-QqmIC0hYaS67N9WNEp9vRh7tj6iM-ADiNSIKLXYVV3lsvyZWOTiAHb1ovC_tST7diWoEfL_AAxwAtE1DNdgg-cKlCNobb6B3OuiY9gZ-8yQeYSsCE/bchce43dm3xxmhg/schem.zip");
        saveFile("map.schem", "https://dl.soep.me/map.schem");
        saveFile("lobby.schem", "https://dl.soep.me/lobby.schem");
        new ListenerService(this);
        PaperCommandManager manager = new PaperCommandManager(this);
     //   getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
     //   getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
     //   getServer().getPluginManager().registerEvents(new WorldLoadListener(this), this);
     //   getServer().getPluginManager().registerEvents(new PlayerSpawnListener(), this);
     //   getServer().getPluginManager().registerEvents(new GameStartListener(this), this);
     //   getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
     //   getServer().getPluginManager().registerEvents(new ServerLoadListener(), this);
     //   getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
     //   getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
     //   getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
     //   getServer().getPluginManager().registerEvents(new InventoryDragListener(), this);
     //   getServer().getPluginManager().registerEvents(new InventoryDropListener(), this);
     //   getServer().getPluginManager().registerEvents(new InventoryMoveItemListener(), this);
     //   getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
     //   getServer().getPluginManager().registerEvents(new GameEndListener(this), this);
     //   getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
     //   getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
     //   getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
     //   getServer().getPluginManager().registerEvents(new PlayerRewardSpecialListener(), this);
     //   getServer().getPluginManager().registerEvents(new PluginEnableListener(), this);
     //   getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
     //   getServer().getPluginManager().registerEvents(new FlagStealListener(), this);
     //   getServer().getPluginManager().registerEvents(new FlagCaptureListener(), this);
     //   getServer().getPluginManager().registerEvents(new AdvancementListener(), this);
     //   getServer().getPluginManager().registerEvents(new EntityPickupItemListener(), this);
     //   getServer().getPluginManager().registerEvents(new PlayerJoinGameListener(this), this);
     //   getServer().getPluginManager().registerEvents(new PlayerLeaveGameListener(this), this);

        manager.registerCommand(new DebugCommand(this));
                // Plugin startup logic
    }

    @Override
    public void onDisable() {
     //   Main.mongo.getConfigCollection().findOneAndUpdate(Filters.eq("_id", 0), new Document("gameCount", Main.mongo.getGameCount()));
        // Plugin shutdown logic
       // this.advancementMain.disable();
    }

    public void saveFile(String resource, String url) {
        if (!new File(getDataFolder(), resource).exists()) {
            try {
                if (!getDataFolder().exists()) {
                    getDataFolder().mkdirs();
                }
                Bukkit.getLogger().log(Level.INFO, "Downloading " + resource + "...");
                Files.copy(new URL(url).openStream(), Paths.get(getDataFolder() + File.separator + resource));
              //  Bukkit.getLogger().log(Level.INFO, "Unzipping " + resource + "...");
             //   Download.unzip(getDataFolder() + File.separator + resource, String.valueOf(getDataFolder()));
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.INFO, "Couldn't create " + resource);
                e.printStackTrace();
            }
        }

    }
}
