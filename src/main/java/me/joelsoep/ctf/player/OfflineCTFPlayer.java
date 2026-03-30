package me.joelsoep.ctf.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.Data;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.logger.LogModule;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * please use this class in async only.
 */
@Data
public class OfflineCTFPlayer {
    private final OfflinePlayer offlinePlayer;
    private final @Nullable CTFPlayer ctfPlayer;
    private final UUID uuid;
    private final @Nullable String name;
    private List<Cosmetic> ownedCosmetics = new ArrayList<>();
    private Integer kills = 0;
    private Integer deaths = 0;
    private Integer captures = 0;
    private Integer experience = 0;
    private Integer coins = 0;
    private Integer winStreak = 0;
    private Double kd = (double) 1;

    private boolean fetched = false;

    public OfflineCTFPlayer(UUID uuid, boolean fetch) {
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.uuid = uuid;
        this.name = offlinePlayer.getName();

        // nullable
        this.ctfPlayer = PlayerService.getPlayer(offlinePlayer.getName());
        if (fetch) {
            fetchData().join();
        }
    }

    public OfflineCTFPlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
        this.uuid = offlinePlayer.getUniqueId();
        this.name = offlinePlayer.getName();

        // nullable
        this.ctfPlayer = PlayerService.getPlayer(offlinePlayer.getName());
        fetchData().join();
    }

    public OfflineCTFPlayer(UUID uuid) {
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.uuid = uuid;
        this.name = offlinePlayer.getName();

        this.ctfPlayer = PlayerService.getPlayer(offlinePlayer.getName());
    }

    public boolean isOnline() {
        return this.getOfflinePlayer().isOnline();
    }

    public CompletableFuture<Boolean> fetchData() {
        CompletableFuture<Boolean> returnFuture = new CompletableFuture<>();
        CompletableFuture<Document> future = MongoManager.findOne(MongoManager.getCollection("player"), Filters.eq("_id", this.uuid.toString()));
        future.thenAccept(result -> {
            if (result.isEmpty()) {
                returnFuture.complete(true);
                return;
            }

            this.kills = result.getInteger("player_kills");
            this.deaths = result.getInteger("player_deaths");
            this.captures = result.getInteger("flag_captures");
            this.coins = result.getInteger("coins");
            this.winStreak = result.getInteger("win_streak");
            this.experience = result.getInteger("experience");
            this.kd = result.getDouble("player_kd");
            returnFuture.complete(true);
        });
        return returnFuture;
    }

    public CompletableFuture<Boolean> save(@Nullable Boolean upsert) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bson filter = Filters.eq("_id", this.uuid.toString());

        Bson update = Updates.combine(
                Updates.set("player_kills", this.kills),
                Updates.set("player_deaths", this.deaths),
                Updates.set("flag_captures", this.captures),
                Updates.set("coins", this.coins),
                Updates.set("win_streak", this.winStreak),
                Updates.set("experience", this.experience),
                Updates.set("player_kd", this.kd)
        );

        upsert = upsert == null ? false : upsert;
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(upsert);
        MongoManager.findOneAndUpdate(MongoManager.getCollection("player"), filter, update, options, new IFindOneSubscriber<Document>() {
            @Override
            public void onResult(Document result) {
                future.complete(true);
            }

            @Override
            public void onError(Throwable throwable) {
                LogModule.PLAYER.warning("An error occured while trying to save OfflineCTFPlayer data.");
                throwable.printStackTrace();
            }
        });
        return future;
    }
}
