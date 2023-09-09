package com.rohnsha.stocksense.serviceAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object obj_service {

    val retrofitServiceStat= Retrofit.Builder()
        .baseUrl("https://wznfqzcqzrov2epwv7me4tm46a0rhvse.lambda-url.ap-south-1.on.aws/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val serviceStatusAPI= retrofitServiceStat.create(interface_service::class.java)

}