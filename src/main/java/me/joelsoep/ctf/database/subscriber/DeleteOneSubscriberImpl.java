package me.joelsoep.ctf.database.subscriber;

import com.mongodb.client.result.DeleteResult;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DeleteOneSubscriberImpl implements Subscriber<DeleteResult> {
    private final IDeleteOneSubscriber handler;
    private boolean received = false;

    public DeleteOneSubscriberImpl(IDeleteOneSubscriber handler) {
        this.handler = handler;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(1);
    }

    @Override
    public void onNext(DeleteResult deleteResult) {
        received = true;
        handler.onResult(deleteResult);
    }

    @Override
    public void onError(Throwable t) {
        received = true;
        handler.onError(t);
    }

    @Override
    public void onComplete() {
        if (!received) {
            handler.onResult(null);
        }
    }
}
