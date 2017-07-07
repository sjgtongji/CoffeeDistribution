package com.distribution.buzztime.coffeedistribution.http

import java.util.*

/**
 * Created by jigangsun on 2017/6/23.
 */

class HttpBaseResp{
    val CODE_SUCCESS = 200 // 调用成功
    val CODE_PARAMS_ERROR = 400 // 参数错误
    val CODE_REQUEST_ERROR = 500 // 请求错误
    val CODE_NETWORK_ERROR = -1 // 网络异常
    val CODE_FILE_ERROR = 504 // 上传文件异常
    val CODE_UPLOAD_LOG = 510 // 服务器需要上传本地日志
    val CODE_UPLOAD_COMMON_LOG = 511 // 服务器需要上传本地日志
    val CODE_UPLOAD_EXCEPTIONS_LOG = 512 // 服务器需要上传操作日志
    val CODE_UPLOAD_DATABASE = 513 // 服务器需要上传操作日志
    val CODE_UPLOAD_OTHER_LOG = 514 // 服务器需要上传其他操作日志
    val CODE_SMS_CONFIRM_OR_CANCEL = 520 // 服务器有订单通过短信确认或者取消
    val CODE_TOKEN_OUT_OF_DATE = 832 // 服务端发现Token可能过期，需要重新登录
    var code : Int = 0;
    var message : String = "";
    var value : String = "{}";
}