package me.joelsoep.ctf.map;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import com.sk89q.util.StringUtil;
import io.papermc.lib.PaperLib;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IStandardSubscriber;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.util.EmptyChunkGenerator;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
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

    protected class Interaction implements Listener {

        public Interaction(Consumer<PlayerInteractEvent> action) {
            Bukkit.getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onInteract(PlayerInteractEvent event) {
                    if (!(event.getPlayer().getWorld().getName().equals(getWorld().getName()))) {
                        return;
                    }
                    action.accept(event);
                }
            }, Main.getInstance());
        }
    }

    public @Nullable World createEmptyWorld() {
        // this has a bug wth the letter d for some reason String sanitizedWorldName = (this.prefix + this.name).replaceAll("[^a-z0-9/._-]", "_");
        WorldCreator worldCreator = new WorldCreator(FileManager.getWorldFolderPath() + this.prefix + this.name);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new EmptyChunkGenerator());
        World world = Bukkit.createWorld(worldCreator);
        if (world == null) {
            LogModule.MAP.warning("World %s is null, attempting to get access to it...".formatted(prefix + name));
            world = Bukkit.getWorld(prefix + name);
        }

        if (world == null) {
            LogModule.MAP.severe("FATAL: World %s couldn't be found. Aborting world creation...".formatted(prefix + name));
            return null;
        }

        world.setKeepSpawnInMemory(false);
        PaperLib.getChunkAtAsync(world.getSpawnLocation(), true);
        return world;
    }

    public CompletableFuture<Boolean> isGloballyEnabled() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        final int[] amount = {0};
        final boolean[] forwarder = new boolean[1];
        Main.getMongoManager().findOne(Main.getMongoManager().getCollection("map"), new Document("name", getPrefix() + getName()), new Subscriber<Document>() {

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(Document result) {
                if (result == null) {
                    Main.getMongoManager().insertOne(Main.getMongoManager().getCollection("map"), new Document("name", getPrefix() + getName()));
                    Main.getMongoManager().findOneAndUpdate(Main.getMongoManager().getCollection("map"), new Document("name", getPrefix() + getName()), Updates.set("enabled", true), new FindOneAndUpdateOptions().upsert(true));
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
                    Main.getMongoManager().insertOne(Main.getMongoManager().getCollection("map"), new Document("name", getPrefix() + getName()));
                    Main.getMongoManager().findOneAndUpdate(Main.getMongoManager().getCollection("map"), new Document("name", getPrefix() + getName()), Updates.set("enabled", true), new FindOneAndUpdateOptions().upsert(false));
                    future.complete(true);
                    return;
                }
                future.complete(forwarder[0]);
            }
        });
        return future;
    }
}
