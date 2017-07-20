package com.distribution.buzztime.coffeedistribution

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
            }
            else -> {

            }
        }
    }
    override fun initViews() {
        navigationBar.setTitle("骑手")
        navigationBar.hiddenLeftButton()
        navigationBar.hiddenRightButton()
        navigationBar.rightBtn.setOnClickListener(this)
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
        LocalBroadcastManager.getInstance(this).registerReceiver(orderReceiver , filter)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orderReceiver)
        super.onDestroy()
    }
    fun getUnreceiveOrders(){
        var callback = object : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onSuccess(t: OrderResp?) {
                Log.e(TAG , gson.toJson(t))

                for(order in t!!.Items){
                    formatOrder(order)
                }
                application.unReceiveOrders = t.Items
                rv_orders.adapter = OrderAdapter(application.unReceiveOrders)

            }

            override fun onFail(resp: HttpBaseResp?) {

            }

            override fun onTestRest(): OrderResp {
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
        var callback = object : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onSuccess(t: OrderResp?) {
                Log.e(TAG , gson.toJson(t))

                for(order in t!!.Items){
                    formatOrder(order)
                }
                rv_orders.adapter = OrderAdapter(t.Items)

            }

            override fun onFail(resp: HttpBaseResp?) {

            }

            override fun onTestRest(): OrderResp {
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
                else -> {Log.e("" , "error")}
            }
        }


        override fun onBindViewHolder(p0: OrderViewHolder, p1: Int) {
            p0.btn_cancel.setOnClickListener(this)
            p0.btn_cancel.setTag(p1)
            when(data[p1].orderState){
                Settings.ORDER_STORE_CONFIRM -> {
                    p0.btn_cancel.text = "接单"
                }
                Settings.ORDER_RIDER_GET -> {
                    p0.btn_cancel.text = "开始配送"
                }
                Settings.ORDER_RIDER_POST -> {
                    p0.btn_cancel.text = "配送完成"
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
        init {
            btn_cancel = binding.root.findViewById(R.id.btn_cancel) as Button
        }
        fun bind(data : Any){
            binding.setVariable(BR.data , data)
            binding.executePendingBindings()
        }
    }

    inner class OrderReciver : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            rl_unfinish.performClick()
        }
    }
}