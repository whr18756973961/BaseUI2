package com.whr.baseui.utils

import android.view.View
import android.widget.Checkable

inline fun <T : View> T.setOnSingleClickListener(
    minTime: Long = 500,
    crossinline block: (T) -> Unit
) {
    setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime > minTime) {
            lastClickTime = System.currentTimeMillis()
            block(this)
        }
    }
}

fun <T : View> T.setOnSingleClickListener(
    onClickListener: View.OnClickListener,
    time: Long = 1000
) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            onClickListener.onClick(this)
        }
    }
}

fun <T : View> T.isFastClick(minTime: Long = 1000): Boolean {
    val currentTimeMillis = System.currentTimeMillis()
    val isFastClick = currentTimeMillis - lastClickTime < minTime
    lastClickTime = currentTimeMillis
    return isFastClick
}

const val TAG_KEY = 1766613144
var <T : View> T.lastClickTime: Long
    set(value) = setTag(TAG_KEY, value)
    get() = getTag(TAG_KEY) as? Long ?: 0


