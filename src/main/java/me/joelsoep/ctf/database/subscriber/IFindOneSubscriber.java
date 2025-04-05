package me.joelsoep.ctf.database.subscriber;

public interface IFindOneSubscriber<T> {

    void onResult(T result);
    void onError(Throwable throwable);
}
