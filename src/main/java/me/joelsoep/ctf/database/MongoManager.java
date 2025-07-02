package me.joelsoep.ctf.database;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.connection.SslSettings;
import com.mongodb.reactivestreams.client.*;
import lombok.Getter;
import me.joelsoep.ctf.database.subscriber.*;
import me.joelsoep.ctf.logger.LogModule;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
public class MongoManager {
    private final MongoClientSettings settings;
    private final MongoClient client;
    private final MongoDatabase database;

    @Getter private static MongoManager instance;

    public MongoManager(ConnectionString connectionString) {
        this.settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(loadCodecRegistry())
                .retryWrites(true)
                .build();

        this.client = MongoClients.create(settings);
        this.database = client.getDatabase("ctf");
        instance = this;
        waitUntilMongoIsReady();
    }

    public CodecRegistry loadCodecRegistry() {
        CodecRegistry defaultRegistry = MongoClientSettings.getDefaultCodecRegistry();

        List<Codec<?>> codecs = new ArrayList<>();
        Reflections reflections = new Reflections("me.joelsoep.ctf.database.codec", Scanners.SubTypes);
        Set<Class<? extends Codec>> codecClasses = reflections.getSubTypesOf(Codec.class);
        codecClasses.forEach(clazz -> {
            try {
                Codec<?> codec = clazz.getDeclaredConstructor().newInstance();
                LogModule.DATABASE.log("Loaded codec %s".formatted(codec.getClass().getName()));
                codecs.add(codec);
            } catch (Exception e) {
                LogModule.DATABASE.severe("Failed to load codec %s:".formatted(clazz.getName()));
                e.printStackTrace();;
            }
        });

        return CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(codecs), defaultRegistry);
    }

    public static void insertOne(MongoCollection<Document> collection, Document document, IFindOneSubscriber<InsertOneResult> Interface) {
        Publisher<InsertOneResult> publisher = collection.insertOne(document);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public static void insertOne(MongoCollection<Document> collection, Document document) {
        Publisher<InsertOneResult> publisher = collection.insertOne(document);
        publisher.subscribe(new EmptySubscriber<InsertOneResult>().getSubscriber());
    }

    public static void find(MongoCollection<Document> collection, @NotNull Bson filter, Subscriber<Document> subscriber) {
        Publisher<Document> publisher = collection.find(filter);
        publisher.subscribe(subscriber);
    }

    public static <TDocument> CompletableFuture<ArrayList<TDocument>> findAll(MongoCollection<TDocument> collection) {
        CompletableFuture<ArrayList<TDocument>> future = new CompletableFuture<>();
        ArrayList<TDocument> documents = new ArrayList<>();
        FindPublisher<TDocument> publisher = collection.find();
        publisher.subscribe(new Subscriber<TDocument>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(TDocument document) {
                documents.add(document);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                future.complete(documents);
            }

            @Override
            public void onComplete() {
                future.complete(documents);
            }
        });
        return future;
    }

    public static void findOne(MongoCollection<Document> collection, @NotNull Bson filter, Subscriber<Document> subscriber) {
        Publisher<Document> publisher = collection.find(filter).first();
        publisher.subscribe(subscriber);
    }

    public static void findOne(MongoCollection<Document> collection, @NotNull Bson filter, IFindOneSubscriber<Document> Interface) {
        Publisher<Document> publisher = collection.find(filter);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public static CompletableFuture<Document> findOne(MongoCollection<Document> collection, @NotNull Bson filter) {
        CompletableFuture<Document> future = new CompletableFuture<>();
        findOne(collection, filter, new IFindOneSubscriber<Document>() {
            @Override
            public void onResult(Document result) {
                future.complete(result);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                future.complete(null);
            }
        });
        return future;
    }

    public static <TDocument> void findOneAndUpdate(MongoCollection<TDocument> collection, @NotNull Bson filter, @NotNull Bson update, FindOneAndUpdateOptions options, IFindOneSubscriber<TDocument> Interface) {
        Publisher<TDocument> publisher = collection.findOneAndUpdate(filter, update, options);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public static <TDocument> void findOneAndUpdate(MongoCollection<TDocument> collection, @NotNull Bson filter, @NotNull Bson update, FindOneAndUpdateOptions options) {
        Publisher<TDocument> publisher = collection.findOneAndUpdate(filter, update, options);
        publisher.subscribe(new EmptySubscriber<TDocument>().getSubscriber());
    }

    public static void countDocuments(MongoCollection<Document> collection, IFindOneSubscriber<Long> Interface) {
        Publisher<Long> publisher = collection.countDocuments();
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public static void deleteOne(MongoCollection<Document> collection, @NotNull Bson filter, IDeleteOneSubscriber Interface) {
        Publisher<DeleteResult> publisher = collection.deleteOne(filter);
        publisher.subscribe(new DeleteOneSubscriberImpl(Interface));
    }

    public static <TDocument> void findOneAndReplace(MongoCollection<TDocument> collection, @NotNull Bson filter, TDocument replacement, FindOneAndReplaceOptions options, IFindOneSubscriber<TDocument> Interface) {
        Publisher<TDocument> publisher = collection.findOneAndReplace(filter, replacement, options);
        publisher.subscribe(new FindOneSubscriber<>(Interface).getSubscriber());
    }

    public static <TDocument> void findOneAndReplace(MongoCollection<TDocument> collection, @NotNull Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
        Publisher<TDocument> publisher = collection.findOneAndReplace(filter, replacement, options);
        publisher.subscribe(new EmptySubscriber<>().getSubscriber());
    }

    public static MongoCollection<Document> getCollection(String name) {
        return getInstance().getDatabase().getCollection(name);
    }

    public static <TDocument> MongoCollection<TDocument> getCollection(String name, Class<TDocument> clazz) {
        return getInstance().getDatabase().getCollection(name, clazz);
    }

    public static void shutdown() {
        LogModule.DATABASE.log("Shutting down MongoDB...");
        getInstance().getClient().close();
        LogModule.DATABASE.log("Successfully shut down MongoDB.");
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
