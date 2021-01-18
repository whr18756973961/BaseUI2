package com.whr.baseui.widget

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import com.whr.baseui.R
import com.whr.baseui.helper.UiCoreHelper



/**
 * 通过在UICore中动态设置的waitdialog的资源文件，生成的dialog
 * Created by whr on 2018/4/19.
 */

class WaitProgressDialog(private val mContext: Context) {
    private var progressBar: Dialog? = null
    private var tv_tips: TextView? = null
    private var pb_view: CircularProgressView? = null
    private val animation =RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

    val waitDialog: Dialog?
        get() {
            if (progressBar == null)
                init("正在加载", false)
            return progressBar
        }

    val isShowing: Boolean
        get() {
            if (progressBar == null)
                init("正在加载", false)
            return progressBar!!.isShowing
        }

    fun show(tips: String) {
        show(tips)
    }

    fun show(isCancle: Boolean) {
        show("正在加载", isCancle)
    }

    fun show(tips: String = "正在加载", isCancle: Boolean = false) {
        init(tips, isCancle)
        progressBar!!.show()
        pb_view?.startAnimation(animation)
    }

    private fun init(tips: String, isCancle: Boolean) {
        var tips = tips
        if (progressBar == null) {
            progressBar = Dialog(mContext, R.style.DialogStyle)
            val view = LayoutInflater.from(mContext)
                .inflate(UiCoreHelper.getProxyA().waitDialogRes(), null)
            progressBar!!.setContentView(view)
            tv_tips = view.findViewWithTag("tv_tips")
            pb_view = view.findViewWithTag("pb_view")
            if (TextUtils.isEmpty(tips))
                tips = "正在加载"
            tv_tips!!.text = tips
            progressBar!!.setCancelable(isCancle)
            progressBar!!.setCanceledOnTouchOutside(false)
            animation.duration = 800
            animation.repeatCount = -1
            animation.interpolator = LinearInterpolator() as Interpolator?
            animation.repeatMode =Animation.RESTART

            //dialog 居中显示
            val window = progressBar?.window
            if (progressBar != null && window != null) {
                val attr = window.attributes
                if (attr != null) {
                    attr.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    attr.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    attr.verticalMargin=-0.08F
                }
            }
        }
    }

    fun dismiss() {
        if (progressBar != null && progressBar!!.isShowing) {
            pb_view?.clearAnimation()
            progressBar!!.dismiss()
        }
    }
}
/**
 * @param
 */
