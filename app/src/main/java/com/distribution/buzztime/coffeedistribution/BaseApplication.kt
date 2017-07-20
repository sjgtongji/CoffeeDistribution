package com.distribution.buzztime.coffeedistribution;

import android.app.Activity
import android.app.Application
import android.content.Context
import com.distribution.buzztime.coffeedistribution.Bean.Order
import com.distribution.buzztime.coffeedistribution.http.LoginResp
import java.util.*

/**
 * Created by jigangsun on 2017/6/14.
 */


/**
 * Created by fg114 on 2016/2/22.
 */
class BaseApplication : Application() {
    var speechHelper : SpeechHelper? = null
    var loginResp : LoginResp? = null
    var unReceiveOrders : MutableList<Order> = mutableListOf<Order>()
    var order : Order? = null
    override fun onCreate() {
        super.onCreate()
        context = this
        speechHelper = SpeechHelper(this)

    }

    companion object {
        var activityStack = Stack<Activity>()
        lateinit var context: Context;
        const val Debug: Boolean = false;
    }
}
