package com.distribution.buzztime.coffeedistribution

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.view.Window
import android.widget.RelativeLayout
import android.widget.Toast
import com.distribution.buzztime.coffeedistribution.http.*
import com.distribution.buzztime.coffeedistribution.view.NavigationBar
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.lang.Long

/**
 * Created by jigangsun on 2017/6/14.
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var navigationBar: NavigationBar;
    lateinit protected var bodyStub: ViewStub;
    lateinit var rootView: RelativeLayout;
    lateinit var bodyView: View;
    lateinit var application: BaseApplication;
    protected var TAG = this.javaClass.simpleName;
    lateinit protected var dialog: ProgressDialog;
    lateinit protected var context: Context;
    var DEBUG : Boolean = false
    var gson : Gson = Gson();
    var http : HttpUtils = HttpUtils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        application = this.getApplication() as BaseApplication
        BaseApplication.activityStack.add(this)
        context = this
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_frame)
        navigationBar = NavigationBar(this,
                findViewById(R.id.title_stub) as ViewStub)
        bodyStub = findViewById(R.id.body_stub) as ViewStub
        bodyStub.layoutResource = layoutResID
        bodyStub.setOnInflateListener(ViewStub.OnInflateListener { stub, inflated ->  initDatas(inflated)})
        bodyView = bodyStub.inflate()
        rootView = findViewById(R.id.main_root) as RelativeLayout
        initNavigateBar()
        initViews()
        initEvents()
    }

    private fun initNavigateBar() {
        navigationBar.leftBtn.visibility = View.VISIBLE
        navigationBar.leftBtn.setOnClickListener { popActivity() }
    }

    protected abstract fun initViews()

    protected abstract fun initEvents()

    protected abstract fun initDatas(view : View)

    fun popActivity() {
        if (BaseApplication.activityStack.size > 0)
            BaseApplication.activityStack.pop().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun finish() {
        BaseApplication.activityStack.remove(this)
        super.finish()
    }

    fun pushActivity(a: Class<*>) {
        startActivity(Intent(this, a))
    }

    fun pushActivity(i: Intent) {
        startActivity(i)
    }

    fun pushActivityForResult(a: Class<*>, requestCode: Int) {
        startActivityForResult(Intent(this, a), requestCode)
    }

    fun pushActivityForResult(a: Intent, requestCode: Int) {
        startActivityForResult(a, requestCode)
    }

    fun pushActivity(a: Class<*>, finishSelf: Boolean) {
        pushActivity(a)
        if (finishSelf) {
            this.finish()
        }
    }

    fun pushActivity(intent: Intent, finishSelf: Boolean) {
        pushActivity(intent)
        if (finishSelf) {
            this.finish()
        }
    }


    fun <T> get(url : String  , callback: HttpCallback<T>){
        http.get(url , callback)
//        var baseResp : HttpBaseResp = HttpBaseResp();
//        url.request().get().rxExecute()
//                .map({r -> r.body().string()})
//                .observeOnMain()
//                .subscribeSafeNext { result ->
//                    Log.d(TAG, result)
//                    if(Settings.TEST_REST){
//                        callback.onSuccess(callback.onTestRest());
//                    }else{
//                        baseResp = toResp(result);
//                        when(baseResp.code){
//                            200 -> {
//                                var resp : T = gson.fromJson(baseResp.value , callback.claze) as T;
//                                callback.onSuccess(resp);
//                            }
//                            else -> {
//                                callback.onFail(baseResp)
//                            }
//                        }
//                    }
//                }
    }

    fun <T> post(url : String  , params : String , callback: HttpCallback<T>){
        http.post(url , params , callback)
    }



    fun showProgressDialog(context: Context,
                           msg: String, listerner: DialogInterface.OnCancelListener?): ProgressDialog {
        return this.showProgressDialog(context, msg, true, 100, listerner)
    }

    fun showDialog() {
        dialog = showProgressDialog(this, "数据加载中,请稍候..." , null)
    }

    fun hideDialog() {
        if (dialog != null) {
            dialog.dismiss()
        }
    }

    fun showProgressDialog(context: Context,
                           msg: String, isIndeterminate: Boolean, max: Int,
                           listerner: DialogInterface.OnCancelListener?): ProgressDialog {
        val pdlg = createProgressDialog(context, msg,
                isIndeterminate, max, listerner)
        try {
            pdlg.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pdlg
    }
    fun createProgressDialog(context: Context,
                             msg: String, isIndeterminate: Boolean, max: Int,
                             listerner: DialogInterface.OnCancelListener?): ProgressDialog {
        val pdlg = ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT)
        pdlg.setTitle("")
        pdlg.setMessage(msg)
        pdlg.isIndeterminate = isIndeterminate
        pdlg.setCanceledOnTouchOutside(false) // 4.0下默认为true，必须显式设为false
        if (isIndeterminate) {
            pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        } else {
            pdlg.max = max
            pdlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        }
        pdlg.setOnCancelListener(listerner)
        // pdlg.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.frame_loading));
        return pdlg
    }

    fun formatDateTime(time : String) : String{
        var result : String = time.replace("/Date(", "").replace(")/", "")
        val time = result.substring(0, result.length - 5)
        val date = Date(Long.parseLong(time))
        val format : String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        return format
    }

    fun formatTime(time : String):String{
        var result : String = time.replace("/Date(", "").replace(")/", "")
        val time = result.substring(0, result.length - 5)
        val date = Date(Long.parseLong(time))
        val format : String = SimpleDateFormat("HH:mm:ss").format(date)
        return format
    }

    fun formatTimeInOneDay(time : kotlin.Long):String{
        val perHour : kotlin.Int = 3600000
        var hour : kotlin.Long = time.div(perHour)
        var min : kotlin.Long  =  time.rem(perHour).div(60000)
        if(min.toInt() == 0) {
            return return hour.toString() + ":" + "00"
        }else{
            return return hour.toString() + ":" + min.toString()
        }
    }

    fun showText(text : String){
        showShort(text)
    }
    fun showShort(text : String){
        Toast.makeText(this, text , Toast.LENGTH_SHORT).show()
    }

    fun showLong(text : String){
        Toast.makeText(this, text , Toast.LENGTH_LONG).show()
    }

    fun showConfirmDialog(requestCode: Int , title : String){
        var intent = Intent(this, DialogActivity::class.java)
        intent.putExtra(Settings.DIALOG_TITLE_KEY , title)
        pushActivityForResult(intent , requestCode)
    }
}