package com.distribution.buzztime.coffeedistribution

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.distribution.buzztime.coffeedistribution.http.Settings
import kotlinx.android.synthetic.main.activity_dialog.*

/**
 * Created by sjg on 2017/7/23.
 */
class DialogActivity : AppCompatActivity() , View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btn_dialog_cancel -> {
                finish()
            }
            R.id.btn_dialog_confirm -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        tv_dialog_title.text = intent.getStringExtra(Settings.DIALOG_TITLE_KEY)
        btn_dialog_cancel.setOnClickListener(this)
        btn_dialog_confirm.setOnClickListener(this)
    }
}