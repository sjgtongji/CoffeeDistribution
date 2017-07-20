package com.distribution.buzztime.coffeedistribution

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.android.databinding.library.baseAdapters.BR
import com.distribution.buzztime.coffeedistribution.BaseActivity
import com.distribution.buzztime.coffeedistribution.Bean.Order
import com.distribution.buzztime.coffeedistribution.R
import com.distribution.buzztime.coffeedistribution.http.HttpBaseResp
import com.distribution.buzztime.coffeedistribution.http.HttpCallback
import com.distribution.buzztime.coffeedistribution.http.OrderResp
import com.distribution.buzztime.coffeedistribution.http.Settings
import kotlinx.android.synthetic.main.activity_order.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * Created by sjg on 2017/6/27.
 */
class OrderActivity : BaseActivity(), View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0!!.id){
            else -> {

            }
        }
    }

    override fun initViews() {
        navigationBar.setTitle("骑手")
        navigationBar.hiddenLeftButton()
        navigationBar.hiddenRightButton()
        navigationBar.rightBtn.setOnClickListener(this)
//        rv_orders.layoutManager = GridLayoutManager(this, 1)
//        rv_orders.adapter = OrderAdapter(orders)

    }

    override fun initEvents() {
        rl_unfinish.setOnClickListener(this)
        rl_finish.setOnClickListener(this)
    }

    override fun initDatas(view: View) {
        var callback = object : HttpCallback<OrderResp>(OrderResp::class.java){
            override fun onSuccess(t: OrderResp?) {
                Log.e(TAG , gson.toJson(t))
            }

            override fun onFail(resp: HttpBaseResp?) {

            }

            override fun onTestRest(): OrderResp {
                return OrderResp()
            }

        }
        var url =
                if(DEBUG){
                    "${Settings.GET_UNASSIGNED_ORDER_URL}?startIndex=1&count=20"
                }else{
                    "${Settings.GET_UNASSIGNED_ORDER_URL}?startIndex=1&count=20"
                }
        get(url , callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_order)
    }

    class OrderAdapter(val data : List<Order>) : RecyclerView.Adapter<OrderViewHolder>() , View.OnClickListener{
        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.btn_receive -> {
                    Log.d("" , "receive" + v.tag)
                }
                R.id.btn_cancel -> {
                    Log.d("" , "cancel" + v.tag)
                }
                else -> {Log.d("" , "error")}
            }
        }


        override fun onBindViewHolder(p0: OrderViewHolder, p1: Int) {
            p0.btn_receive.setOnClickListener(this)
            p0.btn_receive.setTag(p1)
            p0.btn_cancel.setOnClickListener(this)
            p0.btn_cancel.setTag(p1)
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
        var btn_receive : Button
        var btn_cancel : Button
        init {
            btn_receive = binding.root.findViewById(R.id.btn_receive) as Button
            btn_cancel = binding.root.findViewById(R.id.btn_cancel) as Button
        }
        fun bind(data : Any){
            binding.setVariable(BR.data , data)
            binding.executePendingBindings()
        }
    }
}