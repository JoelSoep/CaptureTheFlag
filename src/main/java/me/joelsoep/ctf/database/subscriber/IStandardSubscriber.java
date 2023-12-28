package me.joelsoep.ctf.database.subscriber;

public interface IStandardSubscriber<T> {

    void onResult(T result);
    void onError(Throwable throwable);
}
