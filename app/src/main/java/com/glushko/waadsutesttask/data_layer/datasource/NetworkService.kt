package com.glushko.waadsutesttask.data_layer.datasource

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {

    private const val BASE_URL = "https://waadsu.com/api/"

    fun makeNetworkServiceRxJava(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }

}