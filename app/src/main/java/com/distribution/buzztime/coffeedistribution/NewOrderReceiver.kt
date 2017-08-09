package com.distribution.buzztime.coffeedistribution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import com.distribution.buzztime.coffeedistribution.http.Settings
var ACTION_KEY : String = "com.distribution.buzztime.coffeedistribution.NewOrderReceiver.action"
var ACTION_START : Int = 1
var ACTION_STOP : Int = 2
var ORDER_ID_KEY : String = "com.distribution.buzztime.coffeedistribution.NewOrderReceiver.orderId"

/**
 * Created by sjg on 2017/7/19.
 */
class NewOrderReceiver : BroadcastReceiver() {
    var speechHelper : SpeechHelper? = null;
    var DELAY : Long = 5000
    var START_SPEECH : Int = 0
    var handler : Handler = object  : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            when(msg!!.what){
                START_SPEECH -> {
                    if(msgs.size > 0){
                        for(item in msgs){
                            Log.e("" , item.toString())
                        }
                        speechHelper!!.startSpeaking("您有新的订单")
                        msgs.removeAt(0)
                        if(msgs.size > 0){
                            sendEmptyMessageDelayed(START_SPEECH , DELAY)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    var msgs : MutableList<Int> = mutableListOf()
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(speechHelper == null){
            speechHelper = SpeechHelper(p0!!)
        }
        Log.e("" , "开始播报")
        var action = p1!!.getIntExtra(ACTION_KEY , -1)
        var orderId = p1!!.getIntExtra(ORDER_ID_KEY , -1)
        Log.e("" , action.toString() + "|||" +orderId.toString())
        if(orderId != null && orderId > 0){
            when(action){
                ACTION_START -> {
                    if(!msgs.contains(orderId)){
                        var i : Int = 0
                        while (i < 3){
                            msgs.add(orderId)
                            i++;
                        }
                        handler.sendEmptyMessageDelayed(START_SPEECH , DELAY)
                    }
                }
                ACTION_STOP -> {
                    if(msgs.contains(orderId)){
                        msgs.remove(orderId)
                    }
                }
                else ->{}
            }

        }


//        p0!!.sendBroadcast(Intent(Settings.ACTION_ORDER))

    }
}