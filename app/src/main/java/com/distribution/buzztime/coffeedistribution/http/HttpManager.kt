package com.distribution.buzztime.coffeedistribution.http

/**
 * Created by jigangsun on 2017/6/21.
 */
import android.util.Log
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONObject
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException

annotation class schedule();
val TAG: String = "HttpManager";
// OkHttp
fun String.request(): Request.Builder = Request.Builder().url(this);

fun Request.Builder.execute(): Response = OkHttpClient().newCall(this.build()).execute();

class OkhttpCallback(var onFailure: (Request, IOException) -> Unit, var onResponse: (Response) -> Unit) : Callback {
    public override fun onFailure(request: com.squareup.okhttp.Request, e: java.io.IOException) {
        onFailure.invoke(request, e)
    }

    public override fun onResponse(response: com.squareup.okhttp.Response) {
        onResponse.invoke(response)
    }
}

fun Request.asyncExecute(onFailure: (Request, IOException) -> Unit, onReponse: (Response) -> Unit) {
    OkHttpClient().newCall(this).enqueue(OkhttpCallback(onFailure, onReponse))
}

// ---- OkHttp with RxJava ----
fun String.rxRequest(): Observable<Response> = Observable.defer({ Observable.just("http://www.baidu.com".request().get().execute()) }).subscribeOn(Schedulers.newThread())

fun Request.Builder.rxExecute(): Observable<Response> = Observable.defer({ Observable.just(OkHttpClient().newCall(this.build()).execute()) }).subscribeOn(Schedulers.newThread());

fun <T> Observable<T>.observeOnMain(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.subscribeSafeNext(onNext: (T) -> Unit): Subscription = this.subscribe(onNext, { t -> Log.e(TAG, "", t) }, {});

fun <T> Observable<T>.subscribeSafeCompleted(onCompleted: () -> Unit): Subscription = this.subscribe({}, { t -> Log.e(TAG, "", t) }, onCompleted);

fun toResp(string : String) : HttpBaseResp {
    val jObj = JSONObject(string)
    val jResult = HttpBaseResp()
    if (jObj.has("code"))
    {
        jResult.code = jObj.getInt("code");
    }
    if (jObj.has("message"))
    {
        jResult.message = jObj.getString("message")
    }
    if (jObj.has("value"))
    {
        jResult.value = jObj.getString("value")
    }
    return jResult
}