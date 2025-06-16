package me.joelsoep.ctf.database.subscriber;

import com.mongodb.client.result.DeleteResult;

public interface IDeleteOneSubscriber {

    void onResult(DeleteResult result);
    void onError(Throwable throwable);
}
