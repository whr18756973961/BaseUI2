package com.whr.baseui.fragment

import android.os.Bundle
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.whr.baseui.mvvm.BaseViewModel

abstract class BaseLazyFragment<V : ViewDataBinding, VM : BaseViewModel> :
    BaseMvvmFragment<V, VM>() {
    /*该页面，是否已经准备完毕*/
    private var isPrepared: Boolean = false

    /*该fragment，是否已经执行过懒加载*/
    var isLazyLoaded: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isPrepared = true
    }

    override fun onFragmentVisible() {
        lazyLoad()
    }

    private fun lazyLoad() {
        if (isPrepared && !isLazyLoaded) {
            onLazyLoad()
            isLazyLoaded = true
        }
    }

    override fun onErrorReplyClick() {
        onLazyLoad()
    }

    open abstract fun onLazyLoad()
}