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

    public BackupPlayerDataTask(Plugin plugin) {
        this.plugin = plugin;
        run();
    }

    public void run() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                for (CTFPlayer player : PlayerService.getPlayers().values()) {
                    if (player.getData().getKills() == null) {
                        player.getData().setKills(0);
                    }

                    if (player.getData().getDeaths() == null) {
                        player.getData().setDeaths(0);
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
                            Updates.set("player_kd", roundedNumber)
                    );

                    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);

                    Main.mongo.findOneAndUpdateAsync(Main.mongo.getUserCollection(), Filters.eq("_id", player.getPlayer().getUniqueId().toString()), update, options).thenAccept(result -> {
                        //System.out.println("Update completed: " + result);
                    }).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
                }
            }
        }, 600L, 600L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
