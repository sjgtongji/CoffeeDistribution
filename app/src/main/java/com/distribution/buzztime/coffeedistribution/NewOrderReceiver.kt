package com.distribution.buzztime.coffeedistribution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.distribution.buzztime.coffeedistribution.http.Settings

/**
 * Created by sjg on 2017/7/19.
 */
class NewOrderReceiver : BroadcastReceiver() {
    var speechHelper : SpeechHelper? = null;
    override fun onReceive(p0: Context?, p1: Intent?) {
       if(speechHelper == null){
           speechHelper = SpeechHelper(p0!!)
       }
        speechHelper!!.startSpeaking("您有新的订单")
        p0!!.sendBroadcast(Intent(Settings.ACTION_ORDER))

    }
}