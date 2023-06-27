package com.rohnsha.stocksense

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object rvLTPobject {
    val retrofit= Retrofit.Builder()
        .baseUrl("https://mn46q6wbitsmvcyyqqzyslocl40awcrz.lambda-url.ap-south-1.on.aws/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ltpAPIservice= retrofit.create(rvLTPinterface::class.java)
}