package com.whr.baseui.mvvm

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.whr.baseui.bean.Result
import com.whr.baseui.bean.ViewStatusEnum
import com.whr.baseui.utils.EmptyUtils
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 实现自 BaseMvvmView用于更方便的在ViewModel层中调用view的一些基类方法
 */
open class BaseViewModel : ViewModel(), LifecycleObserver, BaseMvvmView {

    val vStatus: MutableLiveData<Map<String, Any>> = MutableLiveData()

    override fun showWaitDialog() {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWWAITDIALOG
        vStatus.value = viewStatus
    }

    override fun showWaitDialog(message: String) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWWAITDIALOG
        viewStatus["msg"] = message
        vStatus.value = viewStatus
    }

    override fun showWaitDialog(message: String, cancelable: Boolean) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWWAITDIALOG
        viewStatus["msg"] = message
        viewStatus["cancelable"] = cancelable
        vStatus.value = viewStatus
    }

    override fun hideWaitDialog() {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.HIDEWAITDIALOG
        vStatus.value = viewStatus
    }

    override fun showToast(msg: String?) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWTOAST
        viewStatus["msg"] = msg ?: "error"
        vStatus.value = viewStatus
    }


    override fun showStatusEmptyView(emptyMessage: String) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWSTATUSEMPTYVIEW
        viewStatus["msg"] = emptyMessage
        vStatus.value = viewStatus
    }

    override fun showStatusErrorView(emptyMessage: String?) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWSTATUSERRORVIEW
        viewStatus["msg"] = emptyMessage ?: "未知错误"
        vStatus.value = viewStatus
    }

    override fun showStatusLoadingView(loadingMessage: String) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWSTATUSLOADINGVIEW
        viewStatus["msg"] = loadingMessage
        vStatus.value = viewStatus
    }

    override fun showStatusLoadingView(loadingMessage: String, isHasMinTime: Boolean) {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.SHOWSTATUSLOADINGVIEW
        viewStatus["msg"] = loadingMessage
        viewStatus["isHasMinTime"] = isHasMinTime
        vStatus.value = viewStatus
    }

    override fun hideStatusView() {
        var viewStatus = HashMap<String, Any>()
        viewStatus["status"] = ViewStatusEnum.HIDESTATUSVIEW
        vStatus.value = viewStatus
    }

    /**
     * 网络是否可用
     */
    protected var isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }


    suspend fun <T> launchIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    fun launch(tryBlock: suspend CoroutineScope.() -> Unit) {
        launchOnUI {
            tryCatch(tryBlock, {}, {})
        }
    }

    fun launchWithTryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(String?) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit
    ) {
        launchOnUI {
            tryCatch(tryBlock, catchBlock, finallyBlock)
        }
    }

    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(String?) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Throwable) {
                catchBlock(e.message)
            } finally {
                finallyBlock()
            }
        }
    }

    /**
     * 网络请求
     *
     */
    fun <T> launchRequest(
        tryBlock: suspend CoroutineScope.() -> Result<T>?,
        successBlock: suspend CoroutineScope.(T?) -> Unit,
        catchBlock: suspend CoroutineScope.(String?) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit
    ) {
        launchOnUI {
            requestTryCatch(tryBlock, successBlock, catchBlock, finallyBlock)
        }
    }

    suspend fun <T> getResopnse(response: Result<T>?): T? {
        if (response == null || EmptyUtils.isEmpty(response)) return null
        if (response.code == 0) return response.result
        else return null
    }

    private suspend fun <T> requestTryCatch(
        tryBlock: suspend CoroutineScope.() -> Result<T>?,
        successBlock: suspend CoroutineScope.(T?) -> Unit,
        catchBlock: suspend CoroutineScope.(String?) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                var response = tryBlock()
                callResponse(
                    response,
                    {
                        successBlock(response?.result)
                    },
                    {
                        catchBlock(response?.message)
                    }
                )
            } catch (e: Throwable) {
                var errMsg = ""
                when (e) {
                    is UnknownHostException -> {
                        errMsg = "No network..."
                    }
                    is SocketTimeoutException -> {
                        errMsg = "Request timeout..."
                    }
                    is NumberFormatException -> {
                        errMsg = "Request failed, type conversion exception"
                    }
                    else -> {
                        errMsg = e.message.toString()
                        Log.e("xxxxxxxxxx", Gson().toJson(e))
                    }

                }
                catchBlock(errMsg)
            } finally {
                finallyBlock()
            }
        }
    }

    /**
     * 主要用于处理返回的response是否请求成功
     */
    suspend fun <T> callResponse(
        response: Result<T>?, successBlock: suspend CoroutineScope.() -> Unit,
        errorBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            when {
                response == null || EmptyUtils.isEmpty(response) -> errorBlock()
                response.code == 0 -> successBlock()
                else -> errorBlock()
            }
        }
    }
}