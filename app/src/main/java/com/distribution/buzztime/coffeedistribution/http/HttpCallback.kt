package com.distribution.buzztime.coffeedistribution.http

/**
 * Created by jigangsun on 2017/6/23.
 */
abstract class HttpCallback<T>(cls : Class<T>) {
    val claze : Class<T>;
    abstract fun onSuccess(t : T?);
    abstract fun onFail(resp : HttpBaseResp?);
    abstract fun onTestRest() : T;
    init {
        claze = cls;
    }

}