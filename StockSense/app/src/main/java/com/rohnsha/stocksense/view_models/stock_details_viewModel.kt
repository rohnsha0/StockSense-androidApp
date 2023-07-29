package com.rohnsha.stocksense.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rohnsha.stocksense.StockDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date

class stock_details_viewModel: ViewModel() {

    private var ltp by mutableStateOf("")
        private set
    private var prevCls by mutableStateOf("")
        private set

    fun networkCall(symbol: String): List<String> {
        viewModelScope.launch(Dispatchers.IO){
            val response= fetchStockData(symbol)
            ltp= response!!.regularMarketPrice.toString()
            prevCls= response!!.previousClose.toString()
        }
        return listOf(ltp, prevCls)
    }

    fun updateTime(): String {
        val currentTime= Date()
        val formatter = SimpleDateFormat("HH:mm:ss")
        val formattedTime = formatter.format(currentTime)
        return formattedTime
    }

    private fun fetchStockData(symbol: String): com.rohnsha.stocksense.Result.Meta?{
        val url= "https://query1.finance.yahoo.com/v8/finance/chart/$symbol"
        try {
            val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
            val responseBody= response.body
            val json = responseBody?.string()
            Log.d("stockDataFetcher","JSON Response: $json")
            val gson = Gson()
            val stockDataResponse = gson.fromJson(json, StockDataResponse::class.java)
            return stockDataResponse.chart.result[0].meta
        } catch (e: Exception){
            Log.e("stockDataFetcher", "Error fetching stock data: ${e.message}")
            return null
        }
    }

}