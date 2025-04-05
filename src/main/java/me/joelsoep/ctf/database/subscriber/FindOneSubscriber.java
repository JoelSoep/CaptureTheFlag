package me.joelsoep.ctf.database.subscriber;

import lombok.Getter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class FindOneSubscriber<T> {
   @Getter private final Subscriber<T> subscriber;
    public FindOneSubscriber(IFindOneSubscriber<T> Interface) {
        subscriber = new Subscriber<T>() {
            private boolean received = false;
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(T result) {
                received = true;
                Interface.onResult(result);
            }

            @Override
            public void onError(Throwable t) {
                received = true;
                Interface.onError(t);
            }

            @Override
            public void onComplete() {
                if (!received) {
                    Interface.onResult(null);
                }
            }
        };
    }
}
