package com.rohnsha.stocksense.stock_infoAPI

import retrofit2.http.GET
import retrofit2.http.Url

interface stocksInfoInterface {

    @GET
    suspend fun getStockInfo(@Url url: String): dataclass_stocksInfo

}