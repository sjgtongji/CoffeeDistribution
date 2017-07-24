package com.distribution.buzztime.coffeedistribution

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException
import com.baidu.mapapi.navi.BaiduMapNavigation
import com.baidu.mapapi.navi.BaiduMapNavigation.*
import com.baidu.mapapi.navi.NaviParaOption
import com.distribution.buzztime.coffeedistribution.BaseActivity
import com.distribution.buzztime.coffeedistribution.Bean.Order
import com.distribution.buzztime.coffeedistribution.R
import com.distribution.buzztime.coffeedistribution.http.*
import kotlinx.android.synthetic.main.activity_order.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor



/**
 * Created by sjg on 2017/6/27.
 */
class OrderActivity : BaseActivity(), View.OnClickListener{
    var isUnreceive : Boolean = true
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.rl_unfinish -> {
                iv_order.setImageResource(R.mipmap.order_selected)
                iv_distribute.setImageResource(R.mipmap.distribute_unselected)
                tv_unfinish.setTextColor(resources.getColor(R.color.text_yellow))
                tv_finish.setTextColor(resources.getColor(R.color.black))
                isUnreceive = true
                getUnreceiveOrders()
            }
            R.id.rl_finish -> {
                iv_order.setImageResource(R.mipmap.order_unselected)
                iv_distribute.setImageResource(R.mipmap.distribute_selected)
                tv_unfinish.setTextColor(resources.getColor(R.color.black))
                tv_finish.setTextColor(resources.getColor(R.color.text_yellow))
                isUnreceive = false
                getReceivedOrder();
//                showConfirmDialog(10 , "确定要接单?")
            }
            R.id.activity_frame_title_btn_left -> {
                PrefUtils().putString(this@OrderActivity , Settings.NAME_KEY , "")
                PrefUtils().putString(this@OrderActivity , Settings.PWD_KEY , "")
                pushActivity(MainActivity::class.java , true)
            }
            else -> {

            }
        }
    }
    override fun initViews() {
        navigationBar.setTitle("骑手")
        navigationBar.displayLeftButton()
        navigationBar.hiddenRightButton()
        navigationBar.rightBtn.setOnClickListener(this)
        navigationBar.leftBtn.setOnClickListener(this)
        rv_orders.layoutManager = GridLayoutManager(this, 1)
//        rv_orders.adapter = OrderAdapter(orders)

    }

    override fun initEvents() {
        rl_unfinish.setOnClickListener(this)
        rl_finish.setOnClickListener(this)
    }
    var orderReceiver : OrderReciver? = null
    override fun initDatas(view: View) {
        getUnreceiveOrders()
        SyncService().actionStart(this)
        orderReceiver = OrderReciver()
        var filter : IntentFilter = IntentFilter();
        filter.addAction(Settings.ACTION_ORDER)
        registerReceiver(orderReceiver , filter)


    }

    override fun onDestroy() {
        unregisterReceiver(orderReceiver)
        super.onDestroy()
    }
    fun getUnreceiveOrders(){
        showDialog()
        var callback = object : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onSuccess(t: OrderResp?) {
                hideDialog()
                Log.e(TAG , gson.toJson(t))

                for(order in t!!.Items){
                    formatOrder(order)
                }
                application.unReceiveOrders = t.Items
                rv_orders.adapter = OrderAdapter(application.unReceiveOrders)

            }

            override fun onFail(resp: HttpBaseResp?) {
                hideDialog()
                showText(resp!!.message)
            }

            override fun onTestRest(): OrderResp {
                hideDialog()
                return OrderResp()
            }

        }
        var url =
                if(DEBUG){
                    "${Settings.GET_UNASSIGNED_ORDER_URL}?distributionId=${application.loginResp!!.Id}&startIndex=1&count=20"
                }else{
                    "${Settings.GET_UNASSIGNED_ORDER_URL}?distributionId=${application.loginResp!!.Id}&startIndex=1&count=20"
                }
        Log.e(TAG, url)
        get(url , callback)
    }

    fun getReceivedOrder(){
        showDialog()
        var callback = object : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onSuccess(t: OrderResp?) {
                hideDialog()
                Log.e(TAG , gson.toJson(t))

                for(order in t!!.Items){
                    formatOrder(order)
                }
                rv_orders.adapter = OrderAdapter(t.Items)

            }

            override fun onFail(resp: HttpBaseResp?) {
                hideDialog()
                showText(resp!!.message)
            }

            override fun onTestRest(): OrderResp {
                hideDialog()
                return OrderResp()
            }

        }
        var url =
                if(DEBUG){
                    "${Settings.GET_ASSIGNED_ORDER_URL}?distributionId=${application.loginResp!!.Id}&startIndex=1&count=20"
                }else{
                    "${Settings.GET_ASSIGNED_ORDER_URL}?distributionId=${application.loginResp!!.Id}&startIndex=1&count=20"
                }
        Log.e(TAG, url)
        get(url , callback)
    }

    fun formatOrder(order : Order){
        order.amount = String.format("%.2f", order.payMomey)
        order.distributeTime = formatDateTime(order.deliveryMinTime) + "-" + formatTime(order.deliveryMaxTime)
        order.createTimeShow = formatDateTime(order.createTime)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_order)
    }

    fun receiveOrder(order : Order , state : Int){
        var callback = object : HttpCallback<Boolean>(Boolean::class.java){
            override fun onSuccess(t: Boolean?) {
                Log.e(TAG , gson.toJson(t))
                when(state){
                    Settings.ORDER_RIDER_GET -> {
                        getUnreceiveOrders()
                    }
                    Settings.ORDER_RIDER_POST, Settings.ORDER_FINISH-> {
                        getReceivedOrder()
                    }
                    else -> {

                    }
                }
            }

            override fun onFail(resp: HttpBaseResp?) {
                showText(resp!!.message)
            }

            override fun onTestRest(): Boolean {
                return true
            }

        }
        var url =
                if(DEBUG){
                    "${Settings.SET_ORDER_STATE_URL}?distributionId=${application.loginResp!!.Id}&orderId=${order.id}&orderState=${state}"
                }else{
                    "${Settings.SET_ORDER_STATE_URL}?distributionId=${application.loginResp!!.Id}&orderId=${order.id}&orderState=${state}"
                }
        Log.e(TAG, url)
        get(url  , callback)
    }

    lateinit var localClent : LocationClient;
    lateinit var localListener : BDLocationListener;
    fun navigate(order : Order){
        localClent = LocationClient(application)
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll")
        //可选，默认gcj02，设置返回的定位结果坐标系

        val span = 1000
        option.setScanSpan(span)
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true)
        //可选，设置是否需要地址信息，默认不需要

        option.isOpenGps = true
        //可选，默认false,设置是否使用gps

        option.isLocationNotify = true
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false)
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false)
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false)
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        localClent.locOption = option
        localListener = object : BDLocationListener{
            override fun onReceiveLocation(p0: BDLocation?) {
                if (p0!!.getLocType() == BDLocation.TypeNetWorkException) {
                    showText("定位失败，请检查网络是否通畅")
                    return

                } else if (p0!!.getLocType() == BDLocation.TypeCriteriaException) {
                    showText("定位失败，请检查网络是否通畅")
                    return
                }
                Log.e(TAG , p0!!.latitude.toString())
                Log.e(TAG , p0!!.longitude.toString())
                val pt1 : LatLng = LatLng(p0!!.latitude , p0!!.longitude)
                val pt2 : LatLng = LatLng(order.address!!.latitude , order.address!!.longitude)
                val para = NaviParaOption()
                        .startPoint(pt1).endPoint(pt2)
                        .startName("我").endName(order.deliveryAddress)

                try {
                    // 调起百度地图骑行导航
                    openBaiduMapBikeNavi(para, this@OrderActivity)
                } catch (e: BaiduMapAppNotSupportNaviException) {
                    e.printStackTrace()
                }
            }

            override fun onConnectHotSpotMessage(p0: String?, p1: Int) {

            }
        }
        localClent.registerLocationListener(localListener)
        localClent.start()
//        val mLat1 = 39.915291
//        val mLon1 = 116.403857
//// 百度大厦坐标
//        val mLat2 = 40.056858
//        val mLon2 = 116.308194
//        val pt1 = LatLng(mLat1, mLon1)
//        val pt2 = LatLng(mLat2, mLon2)
//
//        // 构建 导航参数
//        val para = NaviParaOption()
//                .startPoint(pt1).endPoint(pt2)
//                .startName("天安门").endName("百度大厦")
//
//        try {
//            // 调起百度地图骑行导航
//            openBaiduMapBikeNavi(para, this@OrderActivity)
//        } catch (e: BaiduMapAppNotSupportNaviException) {
//            e.printStackTrace()
//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                1 -> {
                    when(data!!.getIntExtra(Settings.ORDER_OPERATION_KEY , -1)){
                        Settings.ORDER_OPERATION_CONFIRM -> {
                            receiveOrder(application.order!! , Settings.ORDER_RIDER_GET)
                        }
                        Settings.ORDER_OPERATION_GETED -> {
                            receiveOrder(application.order!! , Settings.ORDER_RIDER_POST)
                        }
                        Settings.ORDER_OPERATION_POSTED -> {
                            receiveOrder(application.order!! , Settings.ORDER_FINISH)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    inner class OrderAdapter(val data : List<Order>) : RecyclerView.Adapter<OrderViewHolder>() , View.OnClickListener{
        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.btn_cancel -> {
                    if(isUnreceive){
                        receiveOrder(data.get(v.tag as Int) , Settings.ORDER_RIDER_GET)
                    }else{
                        var order = data.get(v.tag as Int)
                        when(order.orderState){
                            Settings.ORDER_RIDER_GET -> {
                                receiveOrder(order , Settings.ORDER_RIDER_POST)
                            }
                            Settings.ORDER_RIDER_POST -> {
                                receiveOrder(order , Settings.ORDER_FINISH)
                            }
                        }
                    }
                    Log.e("" , "cancel" + v.tag)

                }
                R.id.ll_order -> {
                    application.order = data[v.tag as Int]
//                    pushActivity(OrderDetailActivity::class.java)
                    var intent = Intent(this@OrderActivity , OrderDetailActivity::class.java)
                    if(isUnreceive){
                        intent.putExtra(Settings.ORDER_OPERATION_KEY , Settings.ORDER_OPERATION_CONFIRM)
                    }else{
                        if(data[v.tag as Int].orderState == Settings.ORDER_RIDER_GET) {
                            intent.putExtra(Settings.ORDER_OPERATION_KEY, Settings.ORDER_OPERATION_GETED)
                        }
                        else if(data[v.tag as Int].orderState == Settings.ORDER_RIDER_POST){
                            intent.putExtra(Settings.ORDER_OPERATION_KEY, Settings.ORDER_OPERATION_POSTED)
                        }
                    }
                    pushActivityForResult(intent , 1)
//                    pushActivity(OrderActivity::class.java)
                }
                R.id.iv_icon -> {
                    var order : Order = data[v.tag as Int]
                    Log.e(TAG , "location")
                    navigate(order)
//                    if(isUnreceive){
//                        //TODO location
//                        navigate(order)
//
//                    }else{
//                        //TODO navigation
//                    }
                }
                else -> {Log.e("" , "error")}
            }
        }


        override fun onBindViewHolder(p0: OrderViewHolder, p1: Int) {
            p0.btn_cancel.setOnClickListener(this)
            p0.btn_cancel.setTag(p1)
            p0.ll_order.setOnClickListener(this)
            p0.ll_order.setTag(p1)
            p0.iv_icon.setTag(p1)
            p0.iv_icon.setOnClickListener(this)
            when(data[p1].orderState){
                Settings.ORDER_STORE_CONFIRM -> {
                    p0.btn_cancel.text = "接单"
                    p0.iv_icon.setImageResource(R.mipmap.navigate)
                }
                Settings.ORDER_RIDER_GET -> {
                    p0.btn_cancel.text = "开始配送"
                    p0.iv_icon.setImageResource(R.mipmap.navigate)
                }
                Settings.ORDER_RIDER_POST -> {
                    p0.btn_cancel.text = "配送完成"
                    p0.iv_icon.setImageResource(R.mipmap.navigate)
                }
            }
            p0.bind(data[p1]);
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderViewHolder {
            val layoutInflater = LayoutInflater.from(p0.context)
            val binding: ViewDataBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.adapter_order, p0, false)

            val holder = OrderViewHolder(binding);
            return holder
        }

        override fun getItemCount(): Int {
            return data.size;
        }

    }

    class OrderViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){
        var btn_cancel : Button
        var ll_order : LinearLayout
        var iv_icon : ImageView
        init {
            btn_cancel = binding.root.findViewById(R.id.btn_cancel) as Button
            ll_order = binding.root.findViewById(R.id.ll_order) as LinearLayout
            iv_icon = binding.root.findViewById(R.id.iv_icon) as ImageView
        }
        fun bind(data : Any){
            binding.setVariable(BR.data , data)
            binding.setVariable(BR.address , (data as Order).address)
            binding.executePendingBindings()
        }
    }

    inner class OrderReciver : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            rl_unfinish.performClick()
        }
    }
}