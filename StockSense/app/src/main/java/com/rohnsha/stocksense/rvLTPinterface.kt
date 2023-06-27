package com.rohnsha.stocksense

import retrofit2.http.GET
import retrofit2.http.Url

interface rvLTPinterface {

    @GET
    suspend fun getLTPrv(@Url url: String):rvLTP
}