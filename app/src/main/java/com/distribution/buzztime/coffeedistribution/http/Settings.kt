package com.distribution.buzztime.coffeedistribution.http

/**
 * Created by jigangsun on 2017/6/21.
 */

class Settings{
    companion object{
        var DIALOG_TITLE_KEY : String = "com.store.buzztime.coffeedistribution.http.dialogtitle"
        var NAME_KEY : String  = "com.store.buzztime.coffeedistribution.http.name"
        var PWD_KEY : String = "com.store.buzztime.coffeedistribution.http.password"
        //0:未确认；1：已确认；2：取消；3：已配送；4：已完成；5：门店接单；6：骑手取餐；7：骑手送餐中
        var ORDER_INIT : Int = 0
        var ORDER_CONFIRM : Int = 1
        var ORDER_CANCEL : Int = 2
        var ORDER_DISTRIBUTE_FINISH : Int = 3
        var ORDER_FINISH : Int = 4
        var ORDER_STORE_CONFIRM : Int = 5
        var ORDER_RIDER_GET : Int = 6
        var ORDER_RIDER_POST : Int = 7

        var PREF_IS_SERVICE_STARTED : String = "com.store.buzztime.coffeedistribution.http.isStarted"
        var ACTION_ORDER : String = "com.store.buzztime.coffeedistribution.http.order"
        var ORDER_OPERATION_KEY : String = "com.store.buzztime.coffeedistribution.http.orderoperation"
        var ORDER_OPERATION_CONFIRM : Int = 1
        var ORDER_OPERATION_GETED: Int = 2
        var ORDER_OPERATION_POSTED : Int = 3
        var DEBUG : Boolean = true;
        var TEST_REST : Boolean = false;
        var SERVER_DEBUG = "http://139.196.228.248:52072/Rest/CoffeeService/"
        var SERVER_RELEASE = "http://waimai.buzztimecoffee.com/Rest/CoffeeService/";
        var LOGIN = "checkDistributionExsit";
        var GET_UNASSIGNED_ORDER = "getUnassignedOrder"
        var GET_ASSIGNED_ORDER = "getAssignedOrderByDistributionId"
        var SET_ORDER_STATE = "setOrderStateByDistributionId"
        lateinit var LOGIN_URL : String
        lateinit var GET_UNASSIGNED_ORDER_URL : String
        lateinit var GET_ASSIGNED_ORDER_URL : String
        lateinit var SET_ORDER_STATE_URL : String
        init {
            if(DEBUG){
                LOGIN_URL = SERVER_DEBUG + LOGIN
                GET_UNASSIGNED_ORDER_URL = SERVER_DEBUG + GET_UNASSIGNED_ORDER
                GET_ASSIGNED_ORDER_URL = SERVER_DEBUG + GET_ASSIGNED_ORDER
                SET_ORDER_STATE_URL = SERVER_DEBUG + SET_ORDER_STATE
            }else{
                LOGIN_URL = SERVER_RELEASE + LOGIN
                GET_UNASSIGNED_ORDER_URL = SERVER_RELEASE + GET_UNASSIGNED_ORDER
                GET_ASSIGNED_ORDER_URL = SERVER_RELEASE + GET_ASSIGNED_ORDER
                SET_ORDER_STATE_URL = SERVER_RELEASE + SET_ORDER_STATE
            }
        }
    }
}