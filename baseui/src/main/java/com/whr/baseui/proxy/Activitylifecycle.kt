package com.whr.baseui.proxy

import android.app.Activity
import android.content.Intent

interface Activitylifecycle {

    fun onActivityCreate(activity: Activity) {}

    fun onActivityStart(activity: Activity) {}

    fun onActivityRestart(activity: Activity) {}

    fun onActivityResume(activity: Activity) {}

    fun onActivityPause(activity: Activity) {}

    fun onActivityStop(activity: Activity) {}

    fun onActivityDestory(activity: Activity) {}

    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {}
}