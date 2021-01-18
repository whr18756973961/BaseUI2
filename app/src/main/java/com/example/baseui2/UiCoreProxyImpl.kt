package com.whr.ktxmvp.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.baseui2.App
import com.example.baseui2.R

import com.whr.baseui.proxy.IUiCoreProxy

/**
 * Created by user on 2019/7/29.
 */

class UiCoreProxyImpl : IUiCoreProxy() {

    override fun loginFragment(): Class<out Fragment>? {
        return null
    }

    override fun getContext(): Context {
        return App.appContext
    }


    override fun colorPrimary(): Int {
        return R.color.colorPrimary
    }

    override fun colorPrimaryDark(): Int {
        return R.color.colorPrimaryDark
    }

    override fun colorAccent(): Int {
        return R.color.colorAccent
    }

    override fun glidePlaceholderRes(): Int {
        return 0
    }

    override fun waitDialogRes(): Int {
        return R.layout.dialog_wait_progressbar
    }

    override fun colorSchemeResources(): IntArray {
        return intArrayOf(R.color.colorAccent)
    }


    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

    }

    override fun headerIdRes(): Int {
        return R.layout.layout_title_white_commom
    }


    override fun headerBackId(): Int {
        return R.id.iv_back
    }

    override fun headerTitleId(): Int {
        return R.id.tv_title
    }

    override fun headerRightId(): Int {
        return R.id.tv_right
    }

    override fun headerRightIconId(): Int {
        return R.id.iv_right
    }

    override fun headerBtmLineId(): Int {
        return R.id.view_deader_line
    }

    override fun onActivityCreate(activity: Activity) {
        lifeCycle?.onActivityCreate(activity)
    }

    override fun onActivityStart(activity: Activity) {}

    override fun onActivityRestart(activity: Activity) {}
    //umeng 统计埋点
    override fun onActivityResume(activity: Activity) {
    }

    //umeng 统计埋点
    override fun onActivityPause(activity: Activity) {
    }

    override fun onActivityStop(activity: Activity) {

    }

    override fun onActivityDestory(activity: Activity) {
        lifeCycle?.onActivityDestory(activity)
    }

    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
    }
}
