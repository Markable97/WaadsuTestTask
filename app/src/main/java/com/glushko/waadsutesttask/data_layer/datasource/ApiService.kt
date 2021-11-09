package com.glushko.waadsutesttask.data_layer.datasource

import io.reactivex.Observable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.GET

interface ApiService {
    companion object{
        //Methods
        const val GET_COORDINATES = "russia.geo.json"
    }

    //@FormUrlEncoded
    @GET(GET_COORDINATES)
    fun getCoordinates(): Observable<ResponseBody>
}