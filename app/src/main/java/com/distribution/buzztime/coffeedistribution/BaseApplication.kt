package com.distribution.buzztime.coffeedistribution;

import android.app.Activity
import android.app.Application
import android.content.Context
import java.util.*

/**
 * Created by jigangsun on 2017/6/14.
 */


/**
 * Created by fg114 on 2016/2/22.
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        var activityStack = Stack<Activity>()
        lateinit var context: Context;
        const val Debug: Boolean = false;
    }
}
