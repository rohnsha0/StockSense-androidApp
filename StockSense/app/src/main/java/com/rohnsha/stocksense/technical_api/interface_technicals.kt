package com.rohnsha.stocksense.technical_api

import retrofit2.http.GET
import retrofit2.http.Url

interface interface_technicals {

    @GET
    suspend fun getTehnicalData(@Url url:String): dataclass_technical
}