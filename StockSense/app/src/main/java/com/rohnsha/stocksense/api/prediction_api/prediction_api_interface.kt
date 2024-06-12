package com.rohnsha.stocksense.api.prediction_api

import retrofit2.http.GET
import retrofit2.http.Url

interface prediction_api_interface {

    @GET
    suspend fun getModelData(@Url url: String): pred_api_dataclass
}