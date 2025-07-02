package me.joelsoep.ctf.map;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.type.GameMap;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
public abstract class Map {

    protected final String name;
    protected final String prefix;
    @Setter protected World world;
    @Setter protected MapState state;
    @Setter private boolean enabled;

    public Map(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
        this.state = MapState.LOADING;
        this.world = null;
    }

    /**
     * Map interactions are a way to implement custom map-specific interactions.
     * For example, clicking on a specific block in a map could result in an interaction.
     */

    protected class Interaction implements Listener {

        public Interaction(Consumer<PlayerInteractEvent> action) {
            Bukkit.getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onInteract(PlayerInteractEvent event) {
                    if (!(event.getPlayer().getWorld().getName().equals(getWorld().getName()))) {
                        return;
                    }

                    if (Main.getGame().getState() != GameState.RUNNING && Map.this instanceof GameMap) {
                        return;
                    }

                    action.accept(event);
                }
            }, Main.getInstance());
        }
    }

    public @Nullable World createEmptyWorld() {
        return MapManager.createEmptyWorld(prefix + name);
    }

    /**
     * Checks if a map should load. It can be enabled/disabled in the database.
     * @return whether the map should load
     */

    public CompletableFuture<Boolean> isGloballyEnabled() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        final int[] amount = {0};
        final boolean[] forwarder = new boolean[1];
        MongoManager.findOne(MongoManager.getCollection("map"), new Document("name", getPrefix() + getName()), new Subscriber<Document>() {

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(Document result) {
                if (result == null) {
                    MongoManager.insertOne(MongoManager.getCollection("map"), new Document("name", getPrefix() + getName()).append("enabled", true));
                    future.complete(true);
                } else {
                    boolean enabled = result.getBoolean("enabled");
                    LogModule.MAP.log(String.valueOf(enabled));
                    future.complete(enabled);
                    forwarder[0] = enabled;
                    amount[0]++;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                LogModule.MAP.log("COMPLETED");
                if (amount[0] == 0) {
                    MongoManager.insertOne(MongoManager.getCollection("map"), new Document("name", getPrefix() + getName()).append("enabled", true));
                    future.complete(true);
                    return;
                }
                future.complete(forwarder[0]);
            }
        });
        return future;
    }

    /**
     * Clears all entities from a map, except for players,
     * as this causes weird behavior.
     */

    public void clearEntities() {
        getWorld().getEntities().forEach(entity -> {
            if (entity instanceof Player player) {
                player.teleport(MapManager.getLobby().getSpawnLocation());
                return;
            }
            entity.remove();
        });
    }
}
