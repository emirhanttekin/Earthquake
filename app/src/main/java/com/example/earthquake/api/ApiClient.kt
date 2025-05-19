package com.example.earthquake.api

import com.example.earthquake.data.remote.KandilliApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.orhanaydogdu.com.tr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: KandilliApi = retrofit.create(KandilliApi::class.java)
}
