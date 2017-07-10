package com.distribution.buzztime.coffeedistribution

import android.content.Context

/**
 * Created by sjg on 2017/7/8.
 */

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import com.iflytek.cloud.*

/**
 * Created by fg114 on 2016/2/3.
 * 讯飞语音合成帮助类
 */
class SpeechHelper(var context : Context){

    var APPID : String = "=59609066"
    var mTts: SpeechSynthesizer
    var speechListener : SpeechListener
    init{
//        SpeechUtility.createUtility(context, SpeechConstant)
        SpeechUtility.createUtility(context, SpeechConstant.APPID + APPID);
        mTts= SpeechSynthesizer.createSynthesizer(context, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        speechListener = SpeechListener();
//        setDefaultParams();
    }
    //    private void setDefaultParams(){
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
//    }
//    public void setSpeed(int speed){
//        if(speed < 0 || speed > 100)
//            throw new InvalidParameterException("SpeechHelper:setSpeed参数无效");
//        mTts.setParameter(SpeechConstant.SPEED , String.valueOf(speed));
//    }
//
//    public void setSpeaker(String name){
//        if(!SPEAKERS.contains(name))
//            throw new InvalidParameterException("SpeechHelper:setSpeaker参数无效");
//        mTts.setParameter(SpeechConstant.VOLUME , name);
//    }
//
//    public void setVolume(int volume){
//        if(volume < 0 || volume > 100)
//            throw new InvalidParameterException("SpeechHelper:setVolume参数无效");
//        mTts.setParameter(SpeechConstant.SPEED , String.valueOf(volume));
//    }
//
    fun startSpeaking(content:String){
        if(content == null)
            return;
        if(mTts == null)
        	return;
        if(mTts.isSpeaking())
            mTts.stopSpeaking();
        mTts.startSpeaking(content, speechListener);
    }
    fun stopSpeaking(){
        mTts.stopSpeaking();
    }
//    public boolean isSpeaking(){
//        return mTts.isSpeaking();
//    }
//    public void destroy(){
//        if(mTts != null){
//            mTts.destroy();
//            mTts = null;
//        }
//        instance = null;
//    }
    open class SpeechListener : SynthesizerListener{
        override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {

        }

        override fun onSpeakBegin() {

        }

        override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {

        }

        override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {

        }

        override fun onSpeakPaused() {

        }

        override fun onSpeakResumed() {

        }

        override fun onCompleted(p0: SpeechError?) {

        }
    }
}
