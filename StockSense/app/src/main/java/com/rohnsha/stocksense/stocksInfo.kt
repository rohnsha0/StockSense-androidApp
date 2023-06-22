package com.rohnsha.stocksense
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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
        val regularMarketPrice: Float,
        val regularMarketTime: Double,
        val previousClose: Float,
        val change: Float,
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
    lateinit var adViewBanner: AdView
    lateinit var adViewBanner2: AdView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_info)

        val inpSymbol= intent.getStringExtra("symbol").toString()

        val stockName= findViewById<TextView>(R.id.tvName)
        val stockLTP= findViewById<TextView>(R.id.stockPrice)
        val stockChange= findViewById<TextView>(R.id.change)
        val topLay= findViewById<LinearLayout>(R.id.topBarLayout)
        val ltpLay= findViewById<LinearLayout>(R.id.LTPLayout)
        val loadingTxt= findViewById<ConstraintLayout>(R.id.loadingView)
        val errorTxt= findViewById<LinearLayout>(R.id.errorLayout)
        val updationPane= findViewById<LinearLayout>(R.id.updationPane)
        val bgMain= findViewById<LinearLayout>(R.id.background_main_dash)
        val updateLTP= findViewById<ImageView>(R.id.refreshTag)
        val stockMarketTime= findViewById<TextView>(R.id.updationnTime)
        val backBTN= findViewById<ImageView>(R.id.backBTN)
        val predView= findViewById<View>(R.id.predictionView)
        val mainContainer= findViewById<ScrollView>(R.id.scrollContainer)
        val toolbar= findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarDash)
        val toolbarTitle= findViewById<TextView>(R.id.dashTitle)
        val appbarLay= findViewById<AppBarLayout>(R.id.appbarLayDash)
        adViewBanner= findViewById(R.id.bannerAdSI)
        adViewBanner2= findViewById(R.id.bannerAdSI2)

        var adClickCount = 0

        val adRequest= AdRequest.Builder().build()
        adViewBanner.loadAd(adRequest)
        adViewBanner.adListener = object: AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                adClickCount= clickProcess(adClickCount)
            }
            override fun onAdFailedToLoad(adError : LoadAdError) {
                super.onAdFailedToLoad(adError)
                adViewBanner.loadAd(adRequest)
            }
            override fun onAdLoaded() {
                if (checkAdStatus()){
                    adViewBanner.visibility= View.VISIBLE
                }else{
                    adViewBanner.visibility= View.GONE
                }
            }
        }

        adViewBanner2.loadAd(adRequest)
        adViewBanner2.adListener = object: AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                adClickCount= clickProcess(adClickCount)
            }
            override fun onAdFailedToLoad(adError : LoadAdError) {
                super.onAdFailedToLoad(adError)
                adViewBanner2.loadAd(adRequest)
            }
            override fun onAdLoaded() {
                if (checkAdStatus()){
                    adViewBanner2.visibility= View.VISIBLE
                }else{
                    adViewBanner2.visibility= View.GONE
                }
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.hide()



        backBTN.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val stockDataFtcher= stockDataFetcher()
        GlobalScope.launch(Dispatchers.IO){
            val stockDataBody= stockDataFtcher.getStockData(inpSymbol)
            launch(Dispatchers.Main){
                if (stockDataBody!=null){
                    appbarLay.visibility= View.VISIBLE
                    supportActionBar?.show()
                    ltpLay.visibility= View.VISIBLE
                    updationPane.visibility= View.VISIBLE
                    bgMain.visibility= View.VISIBLE
                    loadingTxt.visibility= View.GONE
                    mainContainer.setBackgroundColor(ContextCompat.getColor(this@stocksInfo, R.color.dash_bg))

                    predView.setOnClickListener {
                        val intent= Intent(this@stocksInfo, prediction::class.java)
                        intent.putExtra("symbolStock", inpSymbol)
                        intent.putExtra("ltp", stockDataBody.regularMarketPrice.toString())
                        startActivity(intent)
                    }

                    toolbarTitle.text= stockDataBody.symbol

                    stockLTP.text= stockDataBody.regularMarketPrice.toString()
                     val change: Float = (stockDataBody.regularMarketPrice-stockDataBody.previousClose)
                    stockChange.text= String.format("%.2f", change).toFloat().toString()
                    val currentTime = Date()
                    val formatter = SimpleDateFormat("HH:mm:ss")
                    val formattedTime = formatter.format(currentTime)
                    stockMarketTime.text= "Last Updated at: $formattedTime"

                    startPriceUpdateLoop()

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

    private var priceUpdateJob: Job? = null
    private fun startPriceUpdateLoop() {
        val inpSymbol= intent.getStringExtra("symbol").toString()
        priceUpdateJob?.cancel()
        priceUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                updateStockPrice(inpSymbol)
                delay(10000)
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
                    stockLTP.text= "₹" + stockDataBody.regularMarketPrice.toString()
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

    private fun getLastAdClickTimeMillis(): Long {
        val sharedPreferences = this.getSharedPreferences("AdClickSIBanner", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("lastAdClickTimeMillisSI", 0)
    }

    private fun clickProcess(clickCount: Int): Int{
        var adClickCount= clickCount
        val adClickTimeLimitMillis = 2 * 60 * 60 * 1000
        adClickCount++
        if (adClickCount >= 5) {
            val currentTimeMillis = System.currentTimeMillis()
            val lastAdClickTimeMillis = getLastAdClickTimeMillis()
            if (currentTimeMillis - lastAdClickTimeMillis <= adClickTimeLimitMillis) {
                adViewBanner.visibility = View.GONE
                Toast.makeText(this@stocksInfo, "You're abusing app usage policy. It might lead to account suspension!", Toast.LENGTH_LONG).show()
                adClickCount= 0
            }
        }
        updateLastAdClickTimeMillis(System.currentTimeMillis())
        return adClickCount
    }
    private fun updateLastAdClickTimeMillis(currentTimeMillis: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val adReEnableTimeMillis = 3 * 60 * 60 * 1000
        val sharedPreferences = this.getSharedPreferences("AdClickSIBanner", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("lastAdClickTimeMillisSI", currentTimeMillis)
        editor.apply()

        val lastAdClickTimeMillis = sharedPreferences.getLong("lastAdClickTimeMillisSI", 0)
        if (currentTimeMillis - lastAdClickTimeMillis > adReEnableTimeMillis) {
            adViewBanner.visibility = View.VISIBLE
        }
    }

    private fun checkAdStatus(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val adReEnableTimeMillis = 3 * 60 * 60 * 1000
        val sharedPreferences = this.getSharedPreferences("AdClickSIBanner", Context.MODE_PRIVATE)

        val lastAdClickTimeMillis = sharedPreferences.getLong("lastAdClickTimeMillisSI", 0)
        if (currentTimeMillis - lastAdClickTimeMillis < adReEnableTimeMillis) {
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        priceUpdateJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        priceUpdateJob?.cancel()
    }
}