package com.rohnsha.stocksense.api.serviceAPI

import retrofit2.http.GET
import retrofit2.http.Url

interface interface_service {

    @GET
    suspend fun getServiceStatus(@Url url: String): dataclass_service

}