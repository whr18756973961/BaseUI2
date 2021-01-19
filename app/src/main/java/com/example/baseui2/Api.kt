package com.example.baseui2

import com.whr.baseui.bean.Result
import kotlinx.coroutines.Deferred
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {
    @GET("/sug")
    fun requestTestApi(@Query("code") code:String,@Query("q") q:String): Deferred<Result<List<List<String>>>>
}