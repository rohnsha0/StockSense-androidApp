package com.rohnsha.stocksense.prediction_api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object pred_object {
    val retrofit= Retrofit.Builder()
        .baseUrl("https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val predAPIservice = retrofit.create(prediction_api_interface::class.java)
}