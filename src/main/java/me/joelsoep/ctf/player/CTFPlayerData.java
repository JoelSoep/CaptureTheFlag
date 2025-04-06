package me.joelsoep.ctf.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.experience.Level;
import me.joelsoep.ctf.util.MM;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class CTFPlayerData {

    private final CTFPlayer player;
    private final String name;

    private HashMap<String, HashMap<Integer, ItemStack>> sortedKits = new HashMap<>();
    private Date lastSeen;
    private Integer kills = 0;
    private Integer deaths = 0;
    private Integer captures = 0;
    private Integer experience = 0;
    private Integer coins = 0;
    private Integer winStreak = 0;
    private Double kd = (double) 1;

    public CTFPlayerData(CTFPlayer player) {
        this.player = player;
        this.name = player.getPlayer().getName();

        final Document[] resultDocument = new Document[1];
        Main.getMongoManager().find(Main.getMongoManager().getDatabase().getCollection("player"), new Document("_id", player.getPlayer().getUniqueId().toString()), new Subscriber<Document>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(Document document) {
                resultDocument[0] = document;
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                if (resultDocument[0] == null) {
                    Main.getMongoManager().insertOne(Main.getMongoManager().getCollection("player"), new Document(
                            "_id", player.getPlayer().getUniqueId().toString())
                            .append("player_name", name)
                            .append("player_kills", 0)
                            .append("player_deaths", 0)
                            .append("flag_captures", 0)
                            .append("login_streak", 1)
                            .append("player_kd", (double) 1)
                            .append("experience", 0)
                            .append("coins", 0)
                            .append("win_streak", 0)
                    );
                } else {
                    fetchData();
                }
            }
        });
    }

        public void fetchData() {
            final Document[] futureDocument = new Document[1];
            Main.getMongoManager().find(Main.getMongoManager().getDatabase().getCollection("player"), new Document("_id", player.getPlayer().getUniqueId().toString()), new Subscriber<>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(1);
                }

                @Override
                public void onNext(Document document) {
                    futureDocument[0] = document;
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error occurred: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    if (futureDocument[0] == null) {
                        System.out.println("Document not found for player " + player.getPlayer().getName());
                        return;
                    }
                    Document result = futureDocument[0];

                    kills = result.getInteger("player_kills");
                    deaths = result.getInteger("player_deaths");
                    captures = result.getInteger("flag_captures");
                    experience = result.getInteger("experience");
                    coins = result.getInteger("coins");
                    winStreak = result.getInteger("win_streak");
                    player.setLevel(Level.getLevel(player));

                    kd = result.getDouble("player_kd");

                    player.getPlayer().playerListName(MM.deserialize("<gray>" + player.getPlayer().getName() + " ✪" + player.getLevel()));

                    List<Object> resultList = result.getList("sortedKits", Object.class);
                    if (resultList == null) {
                        return;
                    }

                    HashMap<String, HashMap<Integer, Material>> parsedData = new HashMap<>();
                    AtomicInteger position = new AtomicInteger();
                    resultList.forEach(object -> {
                        if (object == null) {
                            return;
                        }
                        String dataString = object.toString();

                        // Remove unnecessary parts of the input string
                        dataString = dataString.replace("Document{{", "").replace("}}", "");

                        // Split the string into parts using "="
                        String[] parts = dataString.split("=");
                        String name = parts[0];
                        StringBuilder innerDataBuilder = new StringBuilder();

                        // Concatenate parts 1 onwards to form innerData
                        for (int i = 1; i < parts.length; i++) {
                            innerDataBuilder.append(parts[i]);
                            if (i < parts.length - 1) {
                                innerDataBuilder.append("=");
                            }
                        }

                        String innerData = innerDataBuilder.toString();
                        HashMap<Integer, Material> innerMap = new HashMap<>();

                        // Split the innerData into key-value pairs using ", "
                        String[] keyValues = innerData.split(", ");
                        List<String> keyValuesList = Arrays.stream(keyValues).toList();
                        LogModule.DATABASE.log(keyValuesList.toString());
                        String hasVersion = keyValuesList.get(keyValuesList.size() - 1);
                        LogModule.DATABASE.log(hasVersion);
                        boolean kitHasNoVersion = !hasVersion.startsWith("version=");
                        LogModule.DATABASE.log(String.valueOf(kitHasNoVersion));
                        LogModule.DATABASE.log("position: " + position);
                        // if the kit is outdated we will skip sorting it, and it won't end up in parsedData.
                        for (String pair : keyValues) {

                            String[] keyValue = pair.split("=");
                            if (keyValue.length == 2) {
                                // check the version of the kit. If changes are made, the old version will be removed from the database
                                // and the player will have to resort it themselves.
                                if (keyValue[0].equals("version") || kitHasNoVersion) {
                                    String version = keyValue[1];
                                    BaseKit versionKit = Main.getKitService().getKit(name);
                                    // if kit no longer exists skip
                                    if (versionKit == null) {
                                        position.getAndIncrement();
                                        return;
                                    }
                                    String latestVersion = versionKit.getVersion();
                                    LogModule.DATABASE.log("ISTRUE: " + (!latestVersion.equals(version) || kitHasNoVersion));
                                    if (!latestVersion.equals(version) || kitHasNoVersion) {
                                        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);
                                        Main.getMongoManager().findOneAndUpdate(Main.getMongoManager().getCollection("player"), Filters.eq("_id", player.getPlayer().getUniqueId().toString()), Updates.unset("sortedKits." + position), options, new IFindOneSubscriber<Document>() {
                                            @Override
                                            public void onResult(Document result) {
                                                if (result == null) {
                                                    onError(null);
                                                    return;
                                                }
                                                player.getWaitingSystemMessages().add("<red>Your sorted kit %s was updated to version %s. Please re-sort it again.".formatted(name, latestVersion));
                                                LogModule.DATABASE.log("reset sorted kit %s of player %s as it was outdated. Old version: %s, new version: %s"
                                                        .formatted(name, player.getPlayer().getName(), keyValue[1], latestVersion));
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                if (throwable == null) {
                                                    LogModule.DATABASE.severe("Redirected from onResult() to OnError() as result was null.");
                                                }
                                                LogModule.DATABASE.severe("Tried to update player kit due to outdated version, but failed!! :: %s, old ver=%s, new ver=%s ".formatted(player.getPlayer().getName(), latestVersion, keyValue[1]));
                                            }
                                            });
                                        LogModule.DATABASE.log("skipped kit %s!!!".formatted(name));
                                        return;
                                        };
                                    continue;
                                }
                                int key = Integer.parseInt(keyValue[0]);
                                String value = keyValue[1];
                                Material material = Material.valueOf(value);
                                innerMap.put(key, material);
                            }
                        }
                        position.getAndIncrement();
                        parsedData.put(name, innerMap);
                    });

// Now you have the parsed data in the parsedData HashMap
                    parsedData.forEach((name, innerMap) -> {
                        LogModule.PLAYER.log("Name: " + name);
                        LogModule.PLAYER.log("Inner Map: " + innerMap);
                    });

                    HashMap<String, HashMap<Integer, ItemStack>> serialized = new HashMap<>();

                    parsedData.forEach((kitName, data) -> {
                        Optional<BaseKit> optionalKit = Main.getKitService().getAllKits().stream().filter(kitIterate -> Objects.equals(kitIterate.getName(), kitName)).findFirst();
                        if (optionalKit.isEmpty()) {
                            return;
                        }

                        LogModule.PLAYER.log("Data: " + data.toString());

                        BaseKit kit = optionalKit.get();
                        HashMap<Integer, ItemStack> mappedStack = new HashMap<>();
                        data.forEach((dataInteger, material) -> {
                            kit.getItems().forEach((kitInteger, item) -> {
                                if (item.getType() == material) {
                                    LogModule.PLAYER.log("Placement: " + dataInteger + " material: " + item.getType());
                                    mappedStack.put(dataInteger, item);
                                }
                            });
                        });
                        serialized.put(kitName, mappedStack);
                    });
                    sortedKits = serialized;
                }
            });
        }
}
