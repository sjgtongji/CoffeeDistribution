package com.distribution.buzztime.coffeedistribution

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.distribution.buzztime.coffeedistribution.Bean.User
import com.distribution.buzztime.coffeedistribution.databinding.ActivityMainBinding
import com.distribution.buzztime.coffeedistribution.http.*
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text


class MainActivity : BaseActivity() , View.OnClickListener{
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_login -> {
                var name : String = et_name.text.toString();
                var password : String = et_password.text.toString();
                user.name = name;
                user.password = password;
                var telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
                var deviceId = telephonyManager.deviceId;
                // TODO login
                var address = "${Settings.LOGIN_URL}?name=$name&passWord=$password&deviceId=$deviceId";
                Log.d(TAG , address)
                var callback = object  : HttpCallback<LoginResp>(LoginResp::class.java){
                    override fun onTestRest(): LoginResp {
                        return LoginResp();
                    }

                    override fun onSuccess(t: LoginResp?) {
                        Log.d(TAG , "success")
                        pushActivity(OrderActivity::class.java)
                    }

                    override fun onFail(t: HttpBaseResp?) {
                        Log.e(TAG , t!!.message);
                    }

                }
                get(address , callback);

            }
            else -> {}
        }
    }

    var user : User = User();
    lateinit var dataBind : ActivityMainBinding;

    override fun initViews() {
        navigationBar.hiddenButtons()
        navigationBar.setTitle("登录")
    }

    override fun initEvents() {

        btn_login.setOnTouchListener(View.OnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    btn_login.setTextColor(resources.getColor(R.color.text_yellow));
                }
                MotionEvent.ACTION_UP -> {
                    btn_login.setTextColor(resources.getColor(R.color.white))
                }
                else -> {}
            }
            false;
        })

        btn_login.setOnClickListener(this)
    }

    override fun initDatas(view : View) {
        dataBind = DataBindingUtil.bind(view , null);
        dataBind.data = user;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_main)
    }


}
