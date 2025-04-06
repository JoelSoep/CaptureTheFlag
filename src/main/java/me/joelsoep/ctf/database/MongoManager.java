package me.joelsoep.ctf.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.Getter;
import me.joelsoep.ctf.database.subscriber.EmptySubscriber;
import me.joelsoep.ctf.database.subscriber.FindOneSubscriber;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.logger.LogModule;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

@Getter
public class MongoManager {
    private final MongoClientSettings settings;
    private final MongoClient client;
    private final MongoDatabase database;

    public MongoManager(ConnectionString connectionString) {
        this.settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        this.client = MongoClients.create(settings);
        this.database = client.getDatabase("ctf");
        waitUntilMongoIsReady();
    }

    public void insertOne(MongoCollection<Document> collection, Document document, IFindOneSubscriber<InsertOneResult> Interface) {
        Publisher<InsertOneResult> publisher = collection.insertOne(document);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public void insertOne(MongoCollection<Document> collection, Document document) {
        Publisher<InsertOneResult> publisher = collection.insertOne(document);
        publisher.subscribe(new EmptySubscriber<InsertOneResult>().getSubscriber());
    }

    public void find(MongoCollection<Document> collection, @NotNull Bson filter, Subscriber<Document> subscriber) {
        Publisher<Document> publisher = collection.find(filter);
        publisher.subscribe(subscriber);
    }

    public void findOne(MongoCollection<Document> collection, @NotNull Bson filter, Subscriber<Document> subscriber) {
        Publisher<Document> publisher = collection.find(filter);
        publisher.subscribe(subscriber);
    }

    public void findOne(MongoCollection<Document> collection, @NotNull Bson filter, IFindOneSubscriber<Document> Interface) {
        Publisher<Document> publisher = collection.find(filter);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public void findOneAndUpdate(MongoCollection<Document> collection, @NotNull Bson filter, @NotNull Bson update, FindOneAndUpdateOptions options, IFindOneSubscriber<Document> Interface) {
        Publisher<Document> publisher = collection.findOneAndUpdate(filter, update, options);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public void findOneAndUpdate(MongoCollection<Document> collection, @NotNull Bson filter, @NotNull Bson update, FindOneAndUpdateOptions options) {
        Publisher<Document> publisher = collection.findOneAndUpdate(filter, update, options);
        publisher.subscribe(new EmptySubscriber<Document>().getSubscriber());
    }

    public void countDocuments(MongoCollection<Document> collection, IFindOneSubscriber<Long> Interface) {
        Publisher<Long> publisher = collection.countDocuments();
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public MongoCollection<Document> getCollection(String name) {
        return getDatabase().getCollection(name);
    }

    private void waitUntilMongoIsReady() {
        int retries = 5;
        int delayMs = 1000;

        LogModule.DATABASE.log("Waiting until database is ready...");
        for (int i = 0; i < retries; i++) {
            try {
                database.runCommand(new Document("ping", 1));
                LogModule.DATABASE.log("Received an appropriate response from the MongoDB server.");
                return;
            } catch (MongoException e) {
                LogModule.KIT.warning("MongoDB not reachable (attempt %d/%d)...".formatted(i + 1, retries));
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("Could not connect to MongoDB after %d attempts.".formatted(retries));
    }
}
