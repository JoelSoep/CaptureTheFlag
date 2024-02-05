package me.joelsoep.ctf.game.task;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;

public class BackupPlayerDataTask {

    private final Plugin plugin;
    private int id;

    public BackupPlayerDataTask() {
        this.plugin = Main.getInstance();
        run();
    }

    public void run() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                backupAll();
            }
        }, 600L, 600L);
    }

    public void backupAll() {
        for (CTFPlayer player : PlayerService.getPlayers().values()) {
            backup(player);
        }
    }

    public void backup(CTFPlayer player) {
        if (player.getData().getKills() == null) {
            player.getData().setKills(0);
        }

        if (player.getData().getDeaths() == null) {
            player.getData().setDeaths(0);
        }

        if (player.getData().getCaptures() == null) {
            player.getData().setCaptures(0);
        }

        if (player.getData().getExperience() == null) {
            player.getData().setExperience(0);
        }

        if (player.getData().getCoins() == null) {
            player.getData().setCoins(0);
        }

        double kd = (double) player.getData().getKills() / (double) player.getData().getDeaths();
        if (Double.isInfinite(kd)) {
            kd = player.getData().getKills();
            if (kd == 0) { kd = 1; }
        }

        DecimalFormat df = new DecimalFormat("#.00");
        String formatted = df.format(kd);
        double roundedNumber = Double.parseDouble(formatted);
        Bson update = Updates.combine(
                Updates.set("player_name", player.getPlayer().getName()),
                Updates.set("player_kills", player.getData().getKills()),
                Updates.set("player_deaths", player.getData().getDeaths()),
                Updates.set("player_kd", roundedNumber),
                Updates.set("flag_captures", player.getData().getCaptures()),
                Updates.set("experience", player.getData().getExperience()),
                Updates.set("coins", player.getData().getCoins())
        );

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);

        Main.getMongoManager().findOneAndUpdate(
                Main.getMongoManager().getCollection("player"),
                Filters.eq("_id", player.getPlayer().getUniqueId().toString()),
                update,
                options
        );
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
