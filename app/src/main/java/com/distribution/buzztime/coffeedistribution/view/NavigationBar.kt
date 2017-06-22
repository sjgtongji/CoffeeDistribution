package com.distribution.buzztime.coffeedistribution.view

/**
 * Created by jigangsun on 2017/6/14.
 */
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.distribution.buzztime.coffeedistribution.R


class NavigationBar(private val act: Activity, protected var vs: ViewStub) : View.OnClickListener {
    lateinit var leftBtn: Button;
    lateinit var rightBtn: Button;
    lateinit var titleText: TextView;
    lateinit var layout: RelativeLayout;
    lateinit var titleView: RelativeLayout;


    var isHidden = false
        private set
    lateinit protected var view: View;

    init {
        val v = initWithlayoutResID(R.layout.activity_frame_navigationbar)
        if(v != null){
            layout = v.findViewById(R.id.activity_frame_title_layout) as RelativeLayout
            titleView = v
                    .findViewById(R.id.activity_frame_titleview_layout) as RelativeLayout
            leftBtn = v.findViewById(R.id.activity_frame_title_btn_left) as Button
            // leftBtn.getPaint().setFakeBoldText(true);
            rightBtn = v.findViewById(R.id.activity_frame_title_btn_right) as Button
            // rightBtn.getPaint().setFakeBoldText(true);
            titleText = v.findViewById(R.id.activity_frame_title_text) as TextView
            leftBtn.setOnClickListener(this)
            rightBtn.setOnClickListener(this)
        }

    }

    fun initWithlayoutResID(resId: Int): View? {
        vs.layoutResource = resId
        view = vs.inflate()
        return view as View?
    }

    fun hidden() {
        isHidden = true
        vs.visibility = View.GONE
    }

    fun display() {
        isHidden = false
        vs.visibility = View.VISIBLE
    }

    fun setBackground(resid: Int) {
        layout.setBackgroundResource(resid)
    }

    fun setBackground(color: String) {
        layout.setBackgroundColor(Color.parseColor(color))
    }


    override fun onClick(v: View) {}

    fun hiddenButtons() {
        leftBtn.visibility = View.GONE
        rightBtn.visibility = View.GONE
    }

    fun displayButtons() {
        leftBtn.visibility = View.VISIBLE
        rightBtn.visibility = View.VISIBLE
    }

    fun hiddenLeftButton() {
        leftBtn.visibility = View.GONE
    }

    fun hiddenRightButton() {
        rightBtn.visibility = View.GONE
    }

    fun displayLeftButton() {
        leftBtn.visibility = View.VISIBLE
    }

    fun displayRightButton() {
        rightBtn.visibility = View.VISIBLE
    }

    fun displayTitle() {
        titleView.visibility = View.VISIBLE
    }

    fun hiddenTitle() {
        titleView.visibility = View.GONE
    }

    fun setTitle(s: String) {
        titleText.text = s
    }

    fun setTitle(strId: Int) {
        titleText.setText(strId)
    }

    fun setTextLeftButton(s: String) {
        leftBtn.text = s
        displayLeftButton()
    }

    fun setTextRightButton(s: String) {
        rightBtn.text = s
        displayRightButton()
    }


    val height: Float
        get() = act.resources.getDimension(R.dimen.navigationbar_height)

}
