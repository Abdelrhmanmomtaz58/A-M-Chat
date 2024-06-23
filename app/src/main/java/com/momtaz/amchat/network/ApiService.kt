package com.momtaz.amchat.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {
    @POST("send")
    fun sendMessage(@HeaderMap headerMap:HashMap<String,String>,@Body message:String):Call<String>
}