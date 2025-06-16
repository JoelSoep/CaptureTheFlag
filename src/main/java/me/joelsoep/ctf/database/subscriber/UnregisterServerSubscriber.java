package me.joelsoep.ctf.database.subscriber;

import com.mongodb.client.result.DeleteResult;
import me.joelsoep.ctf.logger.LogModule;
import java.util.concurrent.CompletableFuture;

/**
 * this class only exists so the jvm wont fuck up during disabling the plugin,
 * because anonymous classes get thrown out too early
 */
public class UnregisterServerSubscriber implements IDeleteOneSubscriber {
    private final CompletableFuture<Boolean> future;

    public UnregisterServerSubscriber(CompletableFuture<Boolean> future) {
        this.future = future;
    }

    @Override
    public void onResult(DeleteResult result) {
        future.complete(result.wasAcknowledged());
    }

    @Override
    public void onError(Throwable throwable) {
        LogModule.CORE.log("Failed to unregister server from database.");
        throwable.printStackTrace();
        future.complete(false);
    }
}