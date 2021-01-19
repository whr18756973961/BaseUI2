package com.whr.baseui.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.google.gson.Gson
import java.lang.Exception

fun ImageView.loadImage(url: String) {
    Glide.with(this.context).load(url)
            .dontAnimate()
            .into(this)
}
fun ImageView.loadImage(url:String,transForm: Transformation<Bitmap>){
    Glide.with(this.context)
            .load(url)
            .dontAnimate()
            .transform(transForm)
            .into(this)
}

/**
 *此处增加一个异常处理，怕服务端传回来异常的数据，导致报错
 */
fun String.parseColorCatchErr():Int{
    return try {
        Color.parseColor(this)
    } catch (err:Exception){
        Color.parseColor("#E0E0E0")
    }
}

/**
 * string 转int，防止报错
 */
fun String.parseIntCatchErr():Int{
    if (this == "") return -1
    return try {
        this.toInt()
    } catch (err:Exception){
        -1
    }
}

