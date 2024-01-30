package me.joelsoep.ctf.player;

import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.experience.Level;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.*;

@Data
public class CTFPlayerData {

    private final CTFPlayer player;
    private final String name;

    private HashMap<String, HashMap<Integer, ItemStack>> sortedKits = new HashMap<>();
    private Date lastSeen;
    private Integer kills;
    private Integer deaths;
    private Integer captures;
    private Integer experience;
    private Double kd;

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
                    );
                    kills = 0;
                    deaths = 0;
                    experience = 0;
                    captures = 0;
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
                    if (kills == null) {
                        kills = 0;
                    }

                    if (deaths == null) {
                        deaths = 0;
                    }

                    if (captures == null) {
                        captures = 0;
                    }

                    if (kd == null) {
                        kd = (double) 1;
                    }

                    if (experience == null) {
                        experience = 0;
                    }

                    kills = result.getInteger("player_kills");
                    deaths = result.getInteger("player_deaths");
                    captures = result.getInteger("flag_captures");
                    experience = result.getInteger("experience");
                    player.setLevel(Level.getLevel(player));

                    kd = result.getDouble("player_kd");

                    List<Object> resultList = result.getList("sortedKits", Object.class);
                    if (resultList == null) {
                        return;
                    }

                    HashMap<String, HashMap<Integer, Material>> parsedData = new HashMap<>();
                    resultList.forEach(object -> {
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
                        for (String pair : keyValues) {
                            String[] keyValue = pair.split("=");
                            if (keyValue.length == 2) {
                                int key = Integer.parseInt(keyValue[0]);
                                String value = keyValue[1];
                                Material material = Material.valueOf(value);
                                innerMap.put(key, material);
                            }

                            parsedData.put(name, innerMap);
                        }
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
