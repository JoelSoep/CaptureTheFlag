package me.joelsoep.ctf.autoBroadcast;

import com.mongodb.Block;
import com.mongodb.async.client.MongoCollection;
import me.joelsoep.ctf.Main;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BroadcastService {

        private final Plugin plugin;

        public BroadcastService(Plugin plugin) {
            this.plugin = plugin;
        }

        MongoCollection<Document> collection = Main.mongo.getAutoBroadcastCollection();


        public void runAll() {
            AtomicInteger count = new AtomicInteger(0);
            Main.mongo.countDocumentsAsync(Main.mongo.getAutoBroadcastCollection()).whenCompleteAsync((result, throwable) -> {
                if (result != null) {
                    count.set(result.intValue());
                    Bukkit.getLogger().info("Broadcasting %s messages.".formatted(count.get()));
                } else {
                    Main.mongo.insertDocumentAsync(Main.mongo.getAutoBroadcastCollection(), new Document(Map.of("_id", "0", "message", "Example message", "duration", 300000)));
                    Bukkit.getLogger().warning("No documents found to autobroadcast!");
                }
            });
            collection.find().forEach(new Block<Document>() {
                @Override
                public void apply(@NotNull Document document) {
                    new BroadcastMessage(plugin).builder()
                            .withDuration(document.getInteger("duration"))
                            .withMessage(document.getString("message"))
                            .withSound(document.getString("sound"))
                            .schedule();
                }
            }, (result, t) -> {});
        }

}
