package com.example.baseui2

import androidx.lifecycle.MutableLiveData
import com.whr.baseui.mvvm.BaseViewModel


class MainViewModel : BaseViewModel() {
    var test: MutableLiveData<String> = MutableLiveData()
}