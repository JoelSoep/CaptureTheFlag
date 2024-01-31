package me.joelsoep.ctf.game.board;

import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Leaderboard {

    private Hologram hologram;
    private Location location;
    private MongoCollection<Document> collection;
    private String key;
    private Object defaultValue;
    private boolean isInteger;
    private String internalName;
    private String fancyName;
    private List<String> lines;
    private String darkColor;
    private String lightColor;

    public Leaderboard(String internalName, String fancyName, MongoCollection<Document> collection, String key, Object defaultValue, boolean isInteger, String darkColor, String lightColor) {
        this.internalName = internalName;
        this.fancyName = fancyName;
        this.collection = collection;
        this.key = key;
        this.defaultValue = defaultValue;
        this.isInteger = isInteger;
        this.darkColor = darkColor;
        this.lightColor = lightColor;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void create() {
        if (location == null) {
            LogModule.UTILITY.warning("Location is null when trying to create Leaderboard %s! Please ensure that the location has been set using setLocation(Location location).".formatted(internalName));
            return;
        }
        this.lines = getLines(collection, key);
        this.runTask(0L, 600L);
    }

    public CompletableFuture<List<String>> sortAsync(MongoCollection<Document> collection, String key) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        List<Document> pipeline = Arrays.asList(
                new Document("$sort", new Document(key, -1)),  // Sort by kills in descending order
                new Document("$limit", 10)  // Limit to the top 10
        );

        List<Document> results = new ArrayList<>();
        AggregatePublisher<Document> aggregate = collection.aggregate(pipeline);
        aggregate.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Document document) {
                results.add(document);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            /**
             * DO NOT TOUCH
             * shit code, but will break if you change shit
             */

            @Override
            public void onComplete() {
                List<String> lines = new ArrayList<>();
                lines.add(fancyName);
                lines.add("");
                int rank = 1;
                for (Document playerData : results) {
                    String playerName = playerData.getString("player_name");
                    Object value;

                    if (!isInteger) {
                        Double doubleValue = playerData.getDouble(key);

                        if (doubleValue != null && !Double.isNaN(doubleValue)) {
                            value = doubleValue;
                        } else {
                            value = (double) defaultValue;
                        }
                    } else {
                        Integer integerValue = playerData.getInteger(key);

                        if (integerValue != null) {
                            value = integerValue;
                        } else {
                            value = (int) defaultValue;
                        }
                    }

                    String line = darkColor + rank + ". &r" + playerName + " - " + lightColor + value.toString();
                    lines.add(line);
                    rank++;
                }
                future.complete(lines);
            }
        });
        return future;
    }

    public @Nullable List<String> getLines(MongoCollection<Document> collection, String key) {
        sortAsync(collection, key).thenAccept(lines -> {
            this.lines = lines;
            if (DHAPI.getHologram(internalName) != null) {
                DHAPI.removeHologram(internalName);
            }
            this.hologram = DHAPI.createHologram(internalName, location, lines);
        }).exceptionally(throwable ->  {
            if (Arrays.toString(throwable.getStackTrace()).contains("class eu.decentsoftware.holograms.api.utils.reflect.ReflectField cannot access a member of class net.minecraft.world.entity.Entity with modifiers \"private static final\"")) {
                return null;
            }
            throwable.printStackTrace();
            return null;
        });
        return lines;
    }

    public void update() {
        DHAPI.removeHologram(internalName);
        this.lines = getLines(this.collection, this.key);
    }

    public void runTask(long delay, long repeat) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, delay, repeat);
    }

}
