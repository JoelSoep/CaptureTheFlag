package me.joelsoep.ctf.game.task;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.discord.DiscordWebhook;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        if (!player.getPlayer().isOnline()) {
            return;
        }

        if (!player.getData().isBackupable() && !Main.getSettings().isAllowBypassDataSuspicionCheck()) {
            return;
        }

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

        if (player.getData().getWinStreak() == null) {
            player.getData().setWinStreak(0);
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
                Updates.set("coins", player.getData().getCoins()), // maybe remove coins from auto backup
                Updates.set("win_streak", player.getData().getWinStreak())
        );

        if (isSuspicious(player) && !Main.getSettings().isAllowBypassDataSuspicionCheck()) {
            LogModule.DATABASE.log("Suspicious player data detected for: " + player.getPlayer().getName() +
                    " (UUID: " + player.getUUID() + "). Skipping backup to avoid corrupt overwrite.");

            DiscordWebhook.sendMessage(new WebhookClient.Callback() {
                @Override
                public void onSuccess(String response) {
                    LogModule.DATABASE.log("Sent webhook for suspicious data.");
                }

                @Override
                public void onFailure(int statusCode, String errorMessage) {
                    LogModule.DATABASE.log("Failed to send webhook for suspicious data.");
                }
            }, """
                    Tried to write suspicious data for player %s, but was halted by data validator!
                    **player_name**: %s
                    **player_kills**: %s
                    **player_deaths**: %s
                    **player_kd**: %s
                    **flag_captures**: %s
                    **experience**: %s
                    **coins**: %s
                    **win_streak**: %s
                    **sortedKits**: %s
                    **ownedCosmetics**: %s
                    **""".formatted(
                            player.getPlayer().getName(),
                    player.getData().getName(),
                    player.getData().getKills(),
                    player.getData().getDeaths(),
                    player.getData().getKd(),
                    player.getData().getCaptures(),
                    player.getData().getExperience(),
                    player.getData().getCoins(),
                    player.getData().getWinStreak(),
                    player.getData().getSortedKits().toString(),
                    player.getData().getOwnedCosmetics().toString()
                    ));
            return;
        }

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);

        MongoManager.findOneAndUpdate(
                MongoManager.getCollection("player"),
                Filters.eq("_id", player.getPlayer().getUniqueId().toString()),
                update,
                options
        );
    }

    public void backupAppliedCosmetics(CTFPlayer player) {
        HashMap<String, String> applied = new HashMap<>();
        for (CosmeticType type : CosmeticType.values()) {
            @Nullable Cosmetic cosmetic = player.getCosmetics().getAppliedCosmetic(type);
            if (cosmetic == null) {
                continue;
            }

            applied.put(type.toString(), cosmetic.getName());
        }
        List<Document> documents = new ArrayList<>();
        applied.forEach((type, name) -> {
            Document document = new Document(type, name);
            documents.add(document);
        });

        Bson updates = Updates.set("appliedCosmetics", documents);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);

        MongoManager.findOneAndUpdate(
                MongoManager.getCollection("player"),
                Filters.eq("_id", player.getUUID().toString()),
                updates,
                options
        );
    }

    public void backupWaitingSystemMessages(CTFPlayer player) {
        List<String> waitingSystemMessages = player.getWaitingSystemMessages();
    }

    public boolean isSuspicious(CTFPlayer player) {
        boolean hasSortedKits = !player.getData().getSortedKits().isEmpty();
        boolean ownsCosmetics = !player.getData().getOwnedCosmetics().isEmpty();
        boolean allStatsZero = player.getData().getKills() == 0 &&
                player.getData().getDeaths() == 0 &&
                player.getData().getExperience() == 0 &&
                player.getData().getCoins() == 0;

        return (hasSortedKits || ownsCosmetics) && allStatsZero;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
