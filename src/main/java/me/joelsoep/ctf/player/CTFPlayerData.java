package me.joelsoep.ctf.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.database.MongoManager;
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
    private EnumMap<CosmeticType, Cosmetic> appliedCosmetics = new EnumMap<>(CosmeticType.class);
    private List<Cosmetic> ownedCosmetics = new ArrayList<>();
    private List<String> storedWaitingSystemMessages = new ArrayList<>();
    private Date lastSeen;
    private Integer kills = 0;
    private Integer deaths = 0;
    private Integer captures = 0;
    private Integer experience = 0;
    private Integer coins = 0;
    private Integer winStreak = 0;
    private Double kd = (double) 1;

    private boolean backupable = false;

    public CTFPlayerData(CTFPlayer player) {
        this.player = player;
        this.name = player.getPlayer().getName();

        final Document[] resultDocument = new Document[1];
        MongoManager.find(MongoManager.getCollection("player"), new Document("_id", player.getPlayer().getUniqueId().toString()), new Subscriber<Document>() {
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
                    MongoManager.insertOne(MongoManager.getCollection("player"), new Document(
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
                    CTFPlayerData.this.setBackupable(true);
                } else {
                    fetchData();
                }
            }
        });
    }

        public void fetchData() {
            final Document[] futureDocument = new Document[1];
            MongoManager.find(MongoManager.getCollection("player"), new Document("_id", player.getPlayer().getUniqueId().toString()), new Subscriber<>() {
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
                    player.getPlayer().kick(MM.deserialize("""
                            <green><bold>CAPTURE THE FLAG</bold>
                            
                            <red>Failed to retrieve your profile from database.
                            <red>Please try again later."""));
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

                    player.getPlayer().playerListName(MM.deserialize("<gray>" + player.getPlayer().getName() + " ⭐" + player.getLevel()));

                    deserializeOwnedCosmetics(result);
                    deserializeAndApplyAppliedCosmetics(result);
                    deserializeKits(result);
                    CTFPlayerData.this.setBackupable(true);
                }
            });
        }

        public void deserializeAndApplyAppliedCosmetics(Document result) {
        List<Object> appliedCosmeticsList = result.getList("appliedCosmetics", Object.class);
        if (appliedCosmeticsList == null || appliedCosmeticsList.isEmpty()) {
            return;
        }

        appliedCosmeticsList.forEach(appliedCosmetic -> {
            String dataString = appliedCosmetic.toString();
            LogModule.CORE.log(dataString);
            dataString = dataString.replace("Document{{", "").replace("}}", "");
            String[] parts = dataString.split("=");
            String type = parts[0];
            String name = parts[1];

            CosmeticType cosmeticType = CosmeticType.valueOf(type);
            Cosmetic cosmetic = Main.getCosmeticsManager().getCosmetic(cosmeticType, name);
            if (cosmetic == null) {
                player.sendMessage(MM.deserialize("Could not find applied cosmetic %s".formatted(name)));
                return;
            }
            player.getCosmetics().setAppliedCosmetic(cosmeticType, cosmetic);
        });

        }

        public void deserializeOwnedCosmetics(Document result) {
            List<Object> ownedCosmeticsList = result.getList("ownedCosmetics", Object.class);
            if (ownedCosmeticsList == null || ownedCosmeticsList.isEmpty()) {
                return;
            }

            LogModule.CORE.log(ownedCosmeticsList.toString());

            ownedCosmeticsList.forEach(ownedCosmetic -> {
                String dataString = ownedCosmetic.toString();
                dataString = dataString.replace("Document{{", "").replace("}}", "");

                // example name for cosmetic would be cosmetic.death_message.TookL
                String[] parts = dataString.split("\\.");
                String prefix = parts[0];
                if (!prefix.toUpperCase(Locale.ROOT).equals("COSMETIC")) {
                    return;
                }

                String typeString = parts[1];
                String name = parts[2];

                CosmeticType cosmeticType = CosmeticType.valueOf(typeString.toUpperCase(Locale.ROOT));
                List<Cosmetic> typeCosmetics = Main.getCosmeticsManager().getCosmetics(cosmeticType);
                if (typeCosmetics == null) {
                    return;
                }

                typeCosmetics.forEach(typeCosmetic -> {
                    String typeCosmeticName = typeCosmetic.getName();
                    if (!Objects.equals(typeCosmeticName, name)) {
                        return;
                    }

                    this.ownedCosmetics.add(typeCosmetic);
                    LogModule.CORE.log("%s owns cosmetic %s of type %s".formatted(player.getPlayer().getName(), name, cosmeticType.toString()));
                });
            });
        }

        public void deserializeKits(Document result) {
            List<Object> resultList = result.getList("sortedKits", Object.class);
            if (resultList == null) {
                return;
            }

            HashMap<String, HashMap<Integer, Material>> parsedData = new HashMap<>();
            AtomicInteger position = new AtomicInteger();
            resultList.forEach(object -> {
                if (object == null) {
                    position.getAndIncrement();
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
                                MongoManager.findOneAndUpdate(MongoManager.getCollection("player"), Filters.eq("_id", player.getPlayer().getUniqueId().toString()), Updates.unset("sortedKits." + position), options, new IFindOneSubscriber<Document>() {
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
                                position.getAndIncrement();
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
}
