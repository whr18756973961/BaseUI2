package com.example.baseui2

import androidx.lifecycle.MutableLiveData
import com.whr.baseui.mvvm.BaseViewModel

class MainViewModel : BaseViewModel() {
    var edit: MutableLiveData<String> = MutableLiveData()
    var test: MutableLiveData<String> = MutableLiveData()
    fun requestTestData() {
        showWaitDialog()
        launchRequest({
            ApiHelper.api().requestTestApi("utf-8", "卫衣").await()
        }, { data: List<List<String>>? ->
            test.value = data.toString()
        }, { errMsg: String? ->
            showToast(errMsg)
        }, {
            hideWaitDialog()
        })
    }
}