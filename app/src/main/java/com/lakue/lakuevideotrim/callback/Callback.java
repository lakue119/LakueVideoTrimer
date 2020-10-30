package com.lakue.lakuevideotrim.callback;

public interface Callback<T,V> {
    void success(T t);
    void failure(V v);
}
