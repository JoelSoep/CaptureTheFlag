package me.joelsoep.ctf.autoBroadcast;

import com.mongodb.reactivestreams.client.MongoCollection;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.logger.LogModule;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BroadcastService {

        MongoCollection<Document> collection = MongoManager.getCollection("autoBroadcast");

        public void runAll() {
            AtomicInteger count = new AtomicInteger(0);

            MongoManager.countDocuments(collection, new IFindOneSubscriber<Long>() {
                @Override
                public void onResult(Long result) {
                    if (result != null) {
                        count.set(result.intValue());
                        LogModule.UTILITY.log("Broadcasting %s messages.".formatted(count.get()));
                    } else {
                        LogModule.UTILITY.warning("No documents found to autobroadcast!");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });

            List<Document> results = new ArrayList<>();
            MongoManager.find(collection, new Document(), new Subscriber<>() {

                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Document document) {
                    if (document != null) {
                        results.add(document);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onComplete() {
                    results.forEach(document -> {
                        BroadcastMessage.builder()
                                .withDuration(document.getInteger("duration"))
                                .withMessage(document.getString("message"))
                                .withSound(document.getString("sound"))
                                .schedule();
                    });
                }
            });
        }

}
