package com.distribution.buzztime.coffeedistribution.http

/**
 * Created by jigangsun on 2017/6/21.
 */

class Settings{
    companion object{
        var DEBUG : Boolean = true;
        var SERVER_DEBUG = "http://139.196.228.248:52072/Rest/CoffeeService/getManager"
        var SERVER_RELEASE = "http://waimai.buzztimecoffee.com/";
        var LOGIN = "login";
        lateinit var LOGIN_URL : String;

        init {
            LOGIN_URL = if(DEBUG){
                SERVER_DEBUG;
            }else{
                SERVER_RELEASE + LOGIN;
            }
        }
    }
}