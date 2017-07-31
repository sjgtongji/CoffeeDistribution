package com.distribution.buzztime.coffeedistribution

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.R.string.cancel
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.distribution.buzztime.coffeedistribution.BaseApplication
import com.distribution.buzztime.coffeedistribution.HttpUtils
import com.distribution.buzztime.coffeedistribution.PrefUtils
import com.distribution.buzztime.coffeedistribution.http.HttpBaseResp
import com.distribution.buzztime.coffeedistribution.http.HttpCallback
import com.distribution.buzztime.coffeedistribution.http.OrderResp
import com.distribution.buzztime.coffeedistribution.http.Settings
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order.*


/**
 * Created by sjg on 2017/7/19.
 */
class SyncService : Service() {
    private val TAG = "SyncService"

    private val ACTION_START = "com.xiaomishu.pospay.service.START"
    private val ACTION_STOP = "com.xiaomishu.pospay.service.STOP"
    private val ACTION_KEEPALIVE = "com.xiaomishu.pospay.service.KEEP_ALIVE"
    private val ACTION_RESTART = "com.xiaomishu.pospay.service.RESTART"

    private val DEFAULT_KEEP_ALIVE_INTERVAL = (1000 * 10).toLong() // 默认的重试时间间隔


    private var mStarted: Boolean = false // 服务是否已启动

    private val mWakeLock: PowerManager.WakeLock? = null // 亮屏控制器

    private val http : HttpUtils = HttpUtils()

    val gson : Gson = Gson()

    var speechHelper : SpeechHelper = SpeechHelper(this)

    /**
     * 启动服务

     * @param ctx
     */
    fun actionStart(ctx: Context) {
        try {
            val intent = Intent(ctx, SyncService::class.java)
            intent.action = ACTION_START
            ctx.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /**
     * 停止服务

     * @param ctx
     */
    fun actionStop(ctx: Context) {
        try {
            val intent = Intent(ctx, SyncService::class.java)
            intent.action = ACTION_STOP
            ctx.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /**
     * 保持服务

     * @param ctx
     */
    fun actionPing(ctx: Context) {
        try {
            val intent = Intent(ctx, SyncService::class.java)
            intent.action = ACTION_KEEPALIVE
            ctx.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    override fun onCreate() {

        try {
            super.onCreate()
            handleCrashedService()
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    override fun onStart(intent: Intent, startId: Int) {
        try {
            super.onStart(intent, startId)

            if (intent.action.equals(ACTION_STOP)) {
                stop()
                stopSelf()
            } else if (intent.action.equals(ACTION_START)) {
                start()
            } else if (intent.action.equals(ACTION_KEEPALIVE) ) {
                keepAlive()
            } else if (intent.action.equals(ACTION_RESTART)) {
                restartIfNecessary()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    override fun onDestroy() {
        try {
            log("this service destroyed!")
            if (mStarted) {
                stop()
            }
            // 防止服务被杀，重新起服务
            actionStart(this@SyncService)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /*--------------------------------------------------------------------------
	| 数据同步
	--------------------------------------------------------------------------*/
    /**
     * 实际处理任务
     */
    var startt: Long = 0

    protected fun startTask() {
        startt = System.currentTimeMillis()
        try {
            getUnreceiveOrders();
            startKeepAlives(DEFAULT_KEEP_ALIVE_INTERVAL)

        } catch (e: Exception) {
            e.printStackTrace()
            startKeepAlives(DEFAULT_KEEP_ALIVE_INTERVAL)
            logE(e)
        } finally {
            // startKeepAlives(DEFAULT_KEEP_ALIVE_INTERVAL);
        }
    }

    fun getUnreceiveOrders(){
        var callback = object : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onSuccess(t: OrderResp?) {
                Log.e(TAG , gson.toJson(t))
                var orders = (application as BaseApplication).unReceiveOrders
                for(order in t!!.Items){
                    if(!orders.contains(order)){
                        speechHelper!!.startSpeaking("您有新的订单")
                        sendBroadcast(Intent(Settings.ACTION_ORDER))
//                        sendBroadcast(Intent(this@SyncService, NewOrderReceiver::class.java))
                        break;
                    }
                }

            }

            override fun onFail(resp: HttpBaseResp?) {
                Log.e(TAG , resp!!.message)
            }

            override fun onTestRest(): OrderResp {
                return OrderResp()
            }

        }
        var resId = PrefUtils().getInt(this, Settings.RES_ID_KEY , -1)
        if(resId > 0){
            var url = "${Settings.GET_UNASSIGNED_ORDER_URL}?distributionId=${resId}&startIndex=1&count=20"

            Log.e(TAG, url)
            http.get(url , callback)
        }

//        var callback = object  : HttpCallback<OrderResp>(OrderResp::class.java){
//            override fun onTestRest(): OrderResp {
//                return OrderResp()
//            }
//
//            override fun onSuccess(t: OrderResp?) {
//                Log.d(TAG , "success" + Gson().toJson(t))
//                var app = getApplication() as BaseApplication
//                for(order in t!!.Items!!){
//                    if(!app.unReceiveOrders!!.contains(order)){
//                        sendBroadcast(Intent(this@SyncService, NewOrderReceiver::class.java))
//                        break;
//                    }
//                }
//
//
////                pushActivity(OrderActivity::class.java)
//            }
//
//            override fun onFail(t: HttpBaseResp?) {
//                Log.e(TAG , t!!.message);
//            }
//
//        }
//        var url = "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=efad271f-6673-4d91-a9f7-abd3d4fe5f87&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10"
//        http.get(url , callback)
//        var app = getApplication() as BaseApplication
//        var url =
//                if(DEBUG){
//                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=efad271f-6673-4d91-a9f7-abd3d4fe5f87&orderState=0,1,2,3,4,5,6,7,8&startIndex=1&count=10"
//                }else{
//                    "${Settings.GET_UNRECEIVE_ORDERS_URL}?resUUID=${app.loginResp!!.resUUID}&orderState=${Settings.ORDER_CONFIRM}&startIndex=1&count=10"
//                }
//        get(url , callback)

    }


    protected fun startKeepAlives(time: Long) {
        var time = time
        try {
            time = if (time > 0) time else DEFAULT_KEEP_ALIVE_INTERVAL// 如果下次循环时间为0，则用默认时间
            val intent = Intent()
            intent.setClass(this, SyncService::class.java)
            intent.action = ACTION_KEEPALIVE

            val pi = PendingIntent.getService(this, 0, intent, 0)
            val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val nextRequestTime = System.currentTimeMillis() + time
            alarmMgr.set(AlarmManager.RTC_WAKEUP, nextRequestTime, pi)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    protected fun stopKeepAlives() {
        try {
            val intent = Intent()
            intent.setClass(this, SyncService::class.java)
            intent.action = ACTION_KEEPALIVE
            val pi = PendingIntent.getService(this, 0, intent, 0)
            val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr.cancel(pi)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }


    /*--------------------------------------------------------------------------
	| 服务操作
	--------------------------------------------------------------------------*/
    /**
     * 启动服务
     */
    @Synchronized protected fun start() {
        try {
            log("Starting service...")
            // 解锁
            if (mStarted) {
                log("Attempt to start service that is already active")
                return
            }

            setStarted(true)

            // 开始处理具体事务
            // startTask();

            startKeepAlives(DEFAULT_KEEP_ALIVE_INTERVAL)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /**
     * 停止服务
     */
    @Synchronized protected fun stop() {
        try {
            log("Stopping service...")
            stopKeepAlives()

            if (!mStarted) {
                log("Attempt to stop service not active.")
                return
            }

            setStarted(false)
            mWakeLock?.release()
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /**
     * 保持服务
     */
    @Synchronized protected fun keepAlive() {
        try {
            startTask()
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /**
     * 重启服务
     */
    @Synchronized protected fun restartIfNecessary() {
        try {
            if (mStarted) {
                stop() // 2013-03-21 add，防止请求在毫秒级别的错误
                startKeepAlives(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /**
     * 处理未正常结束的服务
     */
    protected fun handleCrashedService() {
        try {
            if (wasStarted()) {
                // We probably didn't get a chance to clean up gracefully, so do
                // it now.
                stopKeepAlives()

                // Formally start and attempt connection.
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }

    }

    /*--------------------------------------------------------------------------
	| 其他
	--------------------------------------------------------------------------*/
    /**
     * 返回服务是否已启动

     * @return
     */
    protected fun wasStarted(): Boolean {
        return PrefUtils().getBoolean(this, Settings.PREF_IS_SERVICE_STARTED,
                false)
    }

    /**
     * 设置服务是否已启动

     * @param started
     */
    protected fun setStarted(started: Boolean) {
        PrefUtils().putBoolean(this, Settings.PREF_IS_SERVICE_STARTED, started)
        mStarted = started
    }

    /**
     * 记录日志

     * @param msg
     */
    private fun log(msg: String) {
        Log.d(TAG, msg)
    }

    /**
     * 记录错误

     * @param tr
     */
    private fun logE(tr: Throwable) {
        Log.e(TAG, tr.localizedMessage, tr)
    }
}