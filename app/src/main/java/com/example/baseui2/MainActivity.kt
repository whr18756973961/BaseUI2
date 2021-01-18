package com.example.baseui2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.example.baseui2.databinding.ActivityMainBinding
import com.whr.baseui.activity.BaseMvvmActivity

class MainActivity : BaseMvvmActivity<ActivityMainBinding,MainViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initVariableId(): Int {
        return BR.mainVM
    }

    override fun initView(rootView: View) {

    }

    override fun onResume() {
        super.onResume()
        Log.e("***************", (System.currentTimeMillis()-App.startTime).toString())
    }
}