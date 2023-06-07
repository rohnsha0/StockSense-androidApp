package com.rohnsha.stocksense

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object pred_object {
    val retrofit= Retrofit.Builder()
        .baseUrl("https://web-production-c587.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val predAPIservice = retrofit.create(prediction_api_interface::class.java)
}