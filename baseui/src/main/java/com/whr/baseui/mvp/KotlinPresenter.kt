package com.whr.baseui.mvp

import com.whr.baseui.bean.Result
import com.whr.baseui.utils.EmptyUtils
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 用于配置底层协程操作的presenter、
 *
 *
 */
abstract class KotlinPresenter<V : BaseMvpView> : BaseMvpPresenter<V>() {
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + Job())
    }

    @ExperimentalCoroutinesApi
    override fun detachView() {
        presenterScope.cancel()
    }

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        presenterScope.launch { block() }
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
                    else ->
                        errMsg = e.message.toString()
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
                response.code == 200 -> successBlock()
                else -> errorBlock()
            }
        }
    }
}