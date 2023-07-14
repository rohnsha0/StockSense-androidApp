package com.rohnsha.stocksense.ltpAPI

import retrofit2.http.GET
import retrofit2.http.Url

interface interface_ltp {

    @GET
    suspend fun getLTP(@Url url: String): dataclass_ltp

}