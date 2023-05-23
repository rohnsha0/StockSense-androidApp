package com.example.stocksense
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody

data class StockDataResponse(
    val chart: Chart
)

data class Chart(
    val result: List<Result>
)

data class Result(
    val meta: Meta
) {
    data class Meta(
        val symbol: String,
        val name: String,
        val regularMarketPrice: Number,
        val change: Double,
        val stockVolume: Int
    )
}

class stockDataFetcher{
    suspend fun getStockData(symbol: String): Result.Meta{
        val url= "https://query1.finance.yahoo.com/v8/finance/chart/$symbol"
        val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        val responseBody= response.body
        val json = responseBody?.string()
        Log.d("stockDataFetcher","JSON Response: $json")

        val gson = Gson()
        val stockDataResponse = gson.fromJson(json, StockDataResponse::class.java)
        return stockDataResponse.chart.result[0].meta
    }
    private suspend fun makeRequest(url: String): ResponseBody?{
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body
    }
}


class stocksInfo : AppCompatActivity() {
    private lateinit var stockName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_info)

        val inpSymbol= intent.getStringExtra("symbol").toString()
        val stockName= findViewById<TextView>(R.id.tvName)

        val stockDataFtcher= stockDataFetcher()
        GlobalScope.launch(Dispatchers.IO){
            val stockDataBody= stockDataFtcher.getStockData(inpSymbol)
            launch(Dispatchers.Main){
                stockName.text= stockDataBody.regularMarketPrice.toString()
            }
        }
    }
}