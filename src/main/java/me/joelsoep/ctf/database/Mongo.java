package me.joelsoep.ctf.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.client.model.*;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@SuppressWarnings("deprecated")
public class Mongo {

    private final String connectionString = "mongodb+srv://admin:uFrJ2m8zDDhALE9i@ctf.bh3mqpu.mongodb.net/?retryWrites=true&w=majority";

    private final MongoClient client;
    private final MongoDatabase database;

    private final MongoCollection<Document> configCollection;
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> gameCollection;

    @Setter private Integer gameCount;
    @Setter private Integer userCount;

    public Mongo() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
                .applyToSslSettings(builder -> builder.enabled(true))
                .build();

        this.client = MongoClients.create(settings);
        this.database = client.getDatabase("ctf");
        this.configCollection = database.getCollection("config");
        this.userCollection = database.getCollection("player");
        this.gameCollection = database.getCollection("game");

        CompletableFuture<Void> initFuture = CompletableFuture.runAsync(() -> {
            if (this.countDocumentsAsync(configCollection).join() == 0) {
                this.insertDocumentAsync(configCollection, new Document("_id", 1)
                        .append("gameCount", 0)
                        .append("userCount", 0)
                );
                Bukkit.getLogger().log(Level.INFO, "Created config document in database.");
            }

            this.findOneDocumentAsync(configCollection, new Document("_id", 0)).whenComplete((result, throwable) -> {
                if (result != null) {
                    this.gameCount = result.getInteger("gameCount");
                } else {
                    this.gameCount = 0;
                }
            });

            this.findOneDocumentAsync(configCollection, new Document("_id", 0)).whenComplete((result, throwable) -> {
                if (result != null) {
                    this.userCount = result.getInteger("userCount");
                } else {
                    this.userCount = 0;
                }
            });
        });

        // Wait for the initialization to complete
        initFuture.join();

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
    }

    public CompletableFuture<Void> insertDocumentAsync(com.mongodb.async.client.MongoCollection<Document> collection, Document document) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // Use the asynchronous API to insert the document
        collection.insertOne(document, (result, t) -> {
            if (t != null) {
                future.completeExceptionally(t);
            } else {
                future.complete(null);
            }
        });

        return future;
    }

    public CompletableFuture<Document> findOneDocumentAsync(com.mongodb.async.client.MongoCollection<Document> collection, Document filter) {
        CompletableFuture<Document> future = new CompletableFuture<>();

        // Create a FindIterable with the filter
        com.mongodb.async.client.FindIterable<Document> findIterable = collection.find(filter);

        // Execute the find operation asynchronously
        findIterable.first(new SingleResultCallback<Document>() {
            @Override
            public void onResult(Document result, Throwable t) {
                if (t != null) {
                    future.completeExceptionally(t);
                } else {
                    future.complete(result);
                }
            }
        });

        return future;
    }

    public CompletableFuture<Document> findOneAndUpdateAsync(
            com.mongodb.async.client.MongoCollection<Document> collection,
            Bson filter,
            Bson update,
            FindOneAndUpdateOptions options
    ) {
        CompletableFuture<Document> future = new CompletableFuture<>();

        // Execute the findOneAndUpdate operation asynchronously
        collection.findOneAndUpdate(filter, update, options, new SingleResultCallback<Document>() {
            @Override
            public void onResult(Document result, Throwable t) {
                if (t != null) {
                    future.completeExceptionally(t);
                } else {
                    future.complete(result);
                }
            }
        });

        return future;
    }

    public CompletableFuture<Long> countDocumentsAsync(MongoCollection<Document> collection) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        // Perform the count operation asynchronously
        collection.count(new SingleResultCallback<Long>() {
            @Override
            public void onResult(Long result, Throwable t) {
                if (t != null) {
                    future.completeExceptionally(t);
                } else {
                    future.complete(result);
                }
            }
        });

        return future;
    }

}