package com.distribution.buzztime.coffeedistribution.http

import com.distribution.buzztime.coffeedistribution.Bean.Order


/**
 * Created by sjg on 2017/7/17.
 */
class OrderResp{
    var Items : MutableList<Order> = mutableListOf<Order>();
    var TotalCount : Int = 0;
}