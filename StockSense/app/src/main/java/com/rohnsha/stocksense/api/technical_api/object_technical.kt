package com.rohnsha.stocksense.api.technical_api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object object_technical {

    val retrofit= Retrofit.Builder()
        .baseUrl("https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/technical/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val technicalAPIservice= retrofit.create(interface_technicals::class.java)

}