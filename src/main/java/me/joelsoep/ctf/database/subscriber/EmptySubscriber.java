package me.joelsoep.ctf.database.subscriber;

import lombok.Getter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EmptySubscriber<T> {
    @Getter
    private Subscriber<T> subscriber;
    public EmptySubscriber() {
        subscriber = new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T result) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
    }
}
