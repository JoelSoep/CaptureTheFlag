package me.joelsoep.ctf.game.kit;

import com.mongodb.reactivestreams.client.MongoCollection;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class KitService {
    private final Map<String, BaseKit> kits;

    public KitService() {
        this.kits = new HashMap<>();
        loadKits();
    }

    private void loadKits() {
        // Use reflection to find all classes that extend BaseKit in the specified package
        Reflections reflections = new Reflections(FileManager.KIT_PATH, Scanners.SubTypes);
        Set<Class<? extends BaseKit>> kitClasses = reflections.getSubTypesOf(BaseKit.class);

        // Instantiate and load all found kit classes
        for (Class<? extends BaseKit> kitClass : kitClasses) {
            try {
                BaseKit kit = kitClass.getDeclaredConstructor().newInstance();
                if (!isKitEnabled(kit).join()) {
                    LogModule.KIT.log("Skipping kit %s from registering as it is disabled.".formatted(kit.getName()));
                    continue;
                }
                kits.put(kit.getName().toLowerCase(), kit);
                LogModule.KIT.log("Loaded kit " + kit.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<BaseKit> getAllKits() {
        return new ArrayList<>(kits.values());
    }

    public BaseKit getKit(String kitName) {
        return kits.get(kitName.toLowerCase());
    }

    public void giveKitToPlayer(Player player, String kitName) {
        BaseKit kit = getKit(kitName);
        if (kit != null) {
            kit.giveKitToPlayer(player);
            return;
        }
        MM.deserialize("<#f2180c>This kit doesn't exist. Please report this to an administrator.");
    }

    public @Nullable BaseKit getAppliedKit(Player player) {
        CTFPlayer CTFPlayer = PlayerService.getPlayers().get(player.getName());
        return CTFPlayer.getKit();
    }

    private CompletableFuture<Boolean> isKitEnabled(BaseKit kit) {
        MongoCollection<Document> collection = MongoManager.getCollection("kit");
        String kitName = kit.getName().toLowerCase();
        Document document = new Document("name", kitName);
        final boolean[] enabled = {true};
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        MongoManager.findOne(collection, document, new IFindOneSubscriber<Document>() {
            @Override
            public void onResult(Document result) {
                if (result == null) {
                    LogModule.KIT.log("result for kit %s is null".formatted(kitName));
                 MongoManager.insertOne(collection,
                         new Document("name", kitName)
                                 .append("version", kit.getVersion())
                                 .append("enabled", true)
                 );
                 future.complete(true);
                 return;
                }
                LogModule.KIT.log("found result for kit %s".formatted(kitName));
                enabled[0] = result.getBoolean("enabled");
                future.complete(enabled[0]);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                LogModule.KIT.severe("!!! COULD NOT REACH DATABASE TO CHECK FOR KIT STATUS !!!");
                LogModule.KIT.warning("Enabling kit %s as an error occurred while retrieving it from the database. Please check your database status.".formatted(kitName));
                future.complete(true);
            }
        });
        return future;
    }
}