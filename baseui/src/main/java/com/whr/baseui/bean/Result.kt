package com.whr.baseui.bean

/**
 * Created by user on 2019/7/31.
 */
class Result<T> {

    /**
     * code : 200
     * message :
     * data : {"status":"open"}
     */

    var code: Int = 0
    var message: String? = null
    var data: T? = null
    var exception: String? = null
    var exception_msg: String? = null
    var ok: Boolean = true
}