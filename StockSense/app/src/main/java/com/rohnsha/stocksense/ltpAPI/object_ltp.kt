package com.rohnsha.stocksense.ltpAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object object_ltp {

    val retrofitLtpAPI= Retrofit.Builder()
        .baseUrl("https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ltpAPIService= retrofitLtpAPI.create(interface_ltp::class.java)

}