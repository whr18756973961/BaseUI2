package com.example.baseui2

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

object ApiHelper {
    private var api: Api? = null

    fun api(): Api {
        if (api == null)
            initApi()
        return api!!
    }

    fun initApi() {
        // Header
        val headerInter = Interceptor { chain ->
            val builder = chain.request()
                .newBuilder()
            builder
                .addHeader("Os-Type", "Android")
                .build()
            chain.proceed(builder.build())
        }
        val mOkHttpClient = OkHttpClient()
            .newBuilder()
            .proxy(Proxy.NO_PROXY)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(headerInter)
            .build()
        //网络接口配置
        api = null
        api = Retrofit.Builder()
            .baseUrl("http://suggest.taobao.com")
            .addConverterFactory(ScalarsConverterFactory.create())       //添加字符串的转换器
            .addConverterFactory(GsonConverterFactory.create())          //添加gson的转换器
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())   //添加协程的请求适配器            .client(mOkHttpClient)
            .client(mOkHttpClient)
            .build()
            .create(Api::class.java)
    }


}