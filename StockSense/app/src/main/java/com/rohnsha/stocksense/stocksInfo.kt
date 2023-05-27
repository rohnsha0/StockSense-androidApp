package com.rohnsha.stocksense
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val regularMarketTime: Double,
        val previousClose: Number,
        val change: Double,
        val stockVolume: Int
    )
}

class stockDataFetcher{
    suspend fun getStockData(symbol: String): Result.Meta?{
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
        val stockLTP= findViewById<TextView>(R.id.stockPrice)
        val stockChange= findViewById<TextView>(R.id.change)
        val topLay= findViewById<LinearLayout>(R.id.topBarLayout)
        val ltpLay= findViewById<LinearLayout>(R.id.LTPLayout)
        val loadingTxt= findViewById<TextView>(R.id.loadingView)
        val errorTxt= findViewById<LinearLayout>(R.id.errorLayout)
        val updationPane= findViewById<LinearLayout>(R.id.updationPane)
        val updateLTP= findViewById<ImageView>(R.id.refreshTag)
        val stockMarketTime= findViewById<TextView>(R.id.updationnTime)




        val stockDataFtcher= stockDataFetcher()
        GlobalScope.launch(Dispatchers.IO){
            val stockDataBody= stockDataFtcher.getStockData(inpSymbol)
            launch(Dispatchers.Main){
                if (stockDataBody!=null){
                    topLay.visibility= View.VISIBLE
                    ltpLay.visibility= View.VISIBLE
                    updationPane.visibility= View.VISIBLE
                    loadingTxt.visibility= View.GONE

                    stockName.text= stockDataBody.symbol
                    stockLTP.text= stockDataBody.regularMarketPrice.toString()
                    stockChange.text= (stockDataBody.regularMarketPrice.toInt()-stockDataBody.previousClose.toInt()).toString()
                    val currentTime = Date()
                    val formatter = SimpleDateFormat("HH:mm:ss")
                    val formattedTime = formatter.format(currentTime)
                    stockMarketTime.text= "Last Updated at: $formattedTime"

                    updateLTP.setOnClickListener {
                        updateStockPrice(inpSymbol)
                    }
                    Log.e("predictionStatus", predStats(inpSymbol).toString())

                } else{
                    errorTxt.visibility= View.VISIBLE
                    loadingTxt.visibility= View.GONE
                }
            }
        }
    }

    private fun updateStockPrice(symbol: String){
        val stockDataFtcher= stockDataFetcher()
        val stockMarketTime= findViewById<TextView>(R.id.updationnTime)
        val stockLTP= findViewById<TextView>(R.id.stockPrice)
        GlobalScope.launch(Dispatchers.IO){
            val stockDataBody= stockDataFtcher.getStockData(symbol)
            launch(Dispatchers.Main){
                if(stockDataBody!=null){
                    stockLTP.text= stockDataBody.regularMarketPrice.toString()
                    val currentTime = Date()
                    val formatter = SimpleDateFormat("HH:mm:ss")
                    val formattedTime = formatter.format(currentTime)
                    stockMarketTime.text= "Last Updated at: $formattedTime"
                }
            }
        }
    }

    fun predStats(stockSymbol: String): String? {
        val fileName= "$stockSymbol.tflite"
        val file = File("res/data/$fileName")
        val fileExists= file.exists()
        var prediction: String? = null

        if (fileExists){
            prediction= "model found"
            val length = prediction?.length
        }
        return prediction
    }
}