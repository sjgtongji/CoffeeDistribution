package com.distribution.buzztime.coffeedistribution

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewStub
import android.view.Window
import android.widget.RelativeLayout
import com.distribution.buzztime.coffeedistribution.view.NavigationBar

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

    fun showProgressDialog(text: String, listener: DialogInterface.OnCancelListener?) {
//        dialog = DialogUtils.showProgressDialog(this, text, listener)
    }

    fun showProgressDialog() {
        showProgressDialog("数据加载中,请稍候...")
    }

    fun showProgressDialog(text: String) {
        showProgressDialog(text, null)
    }

    fun hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss()
        }
    }
}