package me.joelsoep.ctf.store;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.client.model.*;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.Data;
import lombok.Getter;
import lombok.extern.java.Log;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.economy.TransactionManager;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.store.items.cosmetic.deathMessage.TookLItem;
import me.joelsoep.ctf.util.MM;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class StoreManager {
    @Getter private static Cloud cloud = new Cloud();
    @Getter private static List<StoreItem> items = new ArrayList<>(); // The public list with available items in store.
    private static List<StoreItem> ITEMS_INCLUDING_UNAVAILABLE = new ArrayList<>(); // private list, including items that are not currently available.

    public StoreManager() {
        loadItems();
      //  Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
        //    @Override
       //     public void run() {
                cloud.verifyItems();
      //      }
      //  });
        Bukkit.getPluginManager().registerEvents(new StoreListener(), Main.getInstance());
    }

    public static void reload() {
        items.clear();
        ITEMS_INCLUDING_UNAVAILABLE.clear();
        loadItems();
        cloud.verifyItems();
    }

    public static @Nullable StoreItem getItem(@NotNull String name) {
        Optional<StoreItem> optional = items.stream().filter(item -> item.getName().equals(name)).findFirst();
        return optional.orElse(null);
    }

    public static void registerItem(@NotNull StoreItem item) {
        items.add(item);
        ITEMS_INCLUDING_UNAVAILABLE.add(item);
        LogModule.COSMETIC.log("Registered store item %s!".formatted(item.getName()));
    }

    public static void unregisterItem(StoreItem item) {
        items.remove(item);
    }

    public static List<StoreItem> getTypeItems(CosmeticType type) {
        List<StoreItem> returnList = new ArrayList<>();
        items.forEach(item -> {
            LogModule.CORE.log(item.getName());
            if (!(item instanceof StoreCosmeticItem cosmeticItem)) {
                return;
            }
            LogModule.CORE.log("yes man making it rn here");
            if (cosmeticItem.getType() == type) {
                LogModule.CORE.log("yes type");
                returnList.add(item);
            }
        });
        return returnList;
    }

    public static void purchase(StoreCosmeticItem item, CTFPlayer player) {
        player.sendMessage(MM.deserialize("<gray>Initializing purchase..."));
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                TransactionManager.createTransaction(player.getUUID(), item, "STORE_PURCHASE_GENERIC");
            }
        });
    }

    public static void loadItems() {
        String dir = "me.joelsoep.ctf.store.items";
        Reflections reflections = new Reflections(dir);

        Set<Class<? extends StoreCosmeticItem>> classes = reflections.getSubTypesOf(StoreCosmeticItem.class);
        classes.forEach(itemClass -> {
            try {
                StoreCosmeticItem item = itemClass.getDeclaredConstructor().newInstance();
                if (!item.isAvailable()) {
                    LogModule.CORE.log("Did not load store item %s as it is not available right now.".formatted(item.getName()));
                    ITEMS_INCLUDING_UNAVAILABLE.add(item);
                    return;
                }
                registerItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // cloud area

    public static class Cloud {
        private static final MongoCollection<StoreItem> collection = MongoManager.getCollection("store", StoreItem.class);

        public ArrayList<StoreItem> getAllItems() {
            return MongoManager.findAll(collection).join();
        }

        public CompletableFuture<Boolean> pushItem(@NotNull StoreItem item) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            String name = item.getName();
            Bson filter = Filters.eq("_id", name);
            LogModule.CORE.log(item.getDisplayName());
            FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
            MongoManager.findOneAndReplace(collection, filter, item.toStoreItem(), options, new IFindOneSubscriber<StoreItem>() {
                @Override
                public void onResult(StoreItem result) {
                    if (result == null) {
                        future.complete(false);
                        return;
                    } else {
                        future.complete(true);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    future.complete(false);
                }
            });
            return future;
        }

        public void verifyItems() {
            List<StoreItem> cloudItems = getAllItems();
            ITEMS_INCLUDING_UNAVAILABLE.forEach(item -> {
                AtomicBoolean contains = new AtomicBoolean(false);
                cloudItems.forEach(cloudItem -> {
                    if (item.getName().equals(cloudItem.getName())) {
                        item.setExpiryDate(cloudItem.getExpiryDate());
                        item.setAvailable(cloudItem.isAvailable());
                        item.setPrice(cloudItem.getPrice());
                        item.setDiscountCoefficient(cloudItem.getDiscountCoefficient());
                        item.setDisplayName(cloudItem.getDisplayName());
                        item.setDisplay(cloudItem.getDisplay());
                        item.checkAndProcessAvailability();
                        if (item.isAvailable()) {
                            items.remove(item);
                            items.add(item);
                            LogModule.COSMETIC.log("Registered store item %s!".formatted(item.getName()));
                        } else {
                            items.remove(item);
                            LogModule.COSMETIC.log("Unregistered store item %s, as it expired!".formatted(item.getName()));
                        }
                        contains.set(true);
                    }
                });

                if (!contains.get()) {
                    pushItem(item);
                    LogModule.CORE.log("Discovered new item %s. Pushing to database.".formatted(item.getName()));
                }
            });
        }
    }
}
