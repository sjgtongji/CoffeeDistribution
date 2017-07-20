package com.distribution.buzztime.coffeedistribution

import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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
                if(DEBUG){
                    user.mobile = "15026648703"
                    user.passWord = "123"
                }else{
                    user.mobile = name
                    user.passWord = password
                }

                // TODO login
                var address =
                        if(DEBUG){
                            "${Settings.LOGIN_URL}?mobile=15026648703&passWord=123";
                        }else{
                            "${Settings.LOGIN_URL}?mobile=${name}&passWord=${password}"
                        }
                Log.e(TAG , address)
                var callback = object  : HttpCallback<LoginResp>(LoginResp::class.java){
                    override fun onTestRest(): LoginResp {
                        return LoginResp();
                    }

                    override fun onSuccess(t: LoginResp?) {
                        Log.e(TAG , "success" + gson.toJson(t))
                        application.loginResp = t
                        pushActivity(OrderActivity::class.java , true)
                    }

                    override fun onFail(t: HttpBaseResp?) {
                        Log.e(TAG , gson.toJson(t))
                        Log.e(TAG , t!!.message);
                    }

                }
                get(address , callback);
//                application.speechHelper!!.startSpeaking("你有新的订单")
//                pushActivity(OrderActivity::class.java , true)
            }
            else -> {}
        }
    }

    var user : User = User();
    lateinit var dataBind : ActivityMainBinding;
    var permissions : MutableList<String> = mutableListOf(
            android.Manifest.permission.READ_PHONE_STATE ,
            android.Manifest.permission.WRITE_SETTINGS ,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CHANGE_NETWORK_STATE);

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        navigationBar.hiddenButtons()
        navigationBar.setTitle("登录")
        for(permission in permissions){
            var hasPermission : Boolean = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            if(!hasPermission){
                requestPermissions( permissions.toTypedArray(), 0);
            }
        }
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
