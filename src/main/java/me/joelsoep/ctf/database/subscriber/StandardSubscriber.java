package me.joelsoep.ctf.database.subscriber;

import com.mongodb.client.result.InsertOneResult;
import lombok.Getter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class StandardSubscriber<T> {
   @Getter private Subscriber<T> subscriber;
    public StandardSubscriber(IStandardSubscriber<T> Interface) {
        subscriber = new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(2);
            }

            @Override
            public void onNext(T result) {
                Interface.onResult(result);
            }

            @Override
            public void onError(Throwable t) {
                Interface.onError(t);
            }

            @Override
            public void onComplete() {

            }
        };
    }
}
