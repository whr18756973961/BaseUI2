package com.whr.baseui.mvvm

import android.app.Dialog

import com.whr.baseui.activity.BaseActivity
import com.whr.baseui.fragment.BaseFragment

/**
 * Created by whr on 2018/6/6.
 */

interface BaseMvvmView {

    fun showWaitDialog()

    fun showWaitDialog(message: String)

    fun showWaitDialog(message: String, cancelable: Boolean)

    fun hideWaitDialog()

    fun showToast(msg: String?)

    fun showStatusEmptyView(emptyMessage: String)

    fun showStatusErrorView(emptyMessage: String?)

    fun showStatusLoadingView(loadingMessage: String)

    fun showStatusLoadingView(loadingMessage: String, isHasMinTime: Boolean)

    fun hideStatusView()
}
