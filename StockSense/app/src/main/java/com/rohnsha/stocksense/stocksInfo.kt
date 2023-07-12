package com.rohnsha.stocksense
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
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
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rohnsha.stocksense.stock_infoAPI.object_stockInfo.stocksInfoAPIservice
import com.rohnsha.stocksense.watchlist_db.watchlists
import com.rohnsha.stocksense.watchlist_db.watchlistsVM
import kotlinx.coroutines.withContext


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
    lateinit var adViewBanner: AdView
    lateinit var adViewBanner2: AdView
    private var mInterstitialAd: InterstitialAd?=null
    lateinit var stockInfoBrnd: String
    private lateinit var mWatchlistModel: watchlistsVM

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
        val errorTxt= findViewById<ConstraintLayout>(R.id.errorLayout)
        val updationPane= findViewById<LinearLayout>(R.id.updationPane)
        val bgMain= findViewById<LinearLayout>(R.id.background_main_dash)
        val updateLTP= findViewById<ImageView>(R.id.refreshTag)
        val stockMarketTime= findViewById<TextView>(R.id.updationnTime)
        val backBTN= findViewById<ImageView>(R.id.backBTN)
        val predView= findViewById<View>(R.id.predictionView)
        val tecchView= findViewById<View>(R.id.technicalView)
        val watchlistView= findViewById<View>(R.id.watchlistView)
        val mainContainer= findViewById<ScrollView>(R.id.scrollContainer)
        val toolbar= findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarDash)
        val toolbarTitle= findViewById<TextView>(R.id.dashTitle)
        val appbarLay= findViewById<AppBarLayout>(R.id.appbarLayDash)
        adViewBanner= findViewById(R.id.bannerAdSI)
        adViewBanner2= findViewById(R.id.bannerAdSI2)
        val watchlistViewTXT= findViewById<TextView>(R.id.btnWatchlist)
        val watchlistIcon= findViewById<ImageView>(R.id.watchlistIcon)
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]


        var wlBtnClickCount = 0
        var adClickCount = 0

        val adRequest= AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-9534797608454346/1015649609", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError?.toString()?.let { Log.d("TAG", it) }
                mInterstitialAd = null
                InterstitialAd.load(this@stocksInfo,"ca-app-pub-9534797608454346/1015649609", adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError?.toString()?.let { Log.d("TAG", it) }
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("TAG", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("TAG", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("tag", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("TAG", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("TAG", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("TAG", "Ad showed fullscreen content.")
            }
        }

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
            val dynamicUrl= "https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws//query/${inpSymbol.uppercase()}"
            try {
                val stocksInfoResponse= stocksInfoAPIservice.getStockInfo(dynamicUrl)
                withContext(Dispatchers.Main){
                    stockInfoBrnd= stocksInfoResponse.stock_name
                    previousCloses(stocksInfoResponse.t1,stocksInfoResponse.t2, stocksInfoResponse.t3, stocksInfoResponse.t4, stocksInfoResponse.t5, stocksInfoResponse.t6)
                }
            } catch (e: Exception){
                stockInfoBrnd= inpSymbol.substringBefore('.').uppercase()
                Log.e("errorName", e.toString())
            }

            Log.d("searchingDB", mWatchlistModel.searchWatchlistsDB(inpSymbol.uppercase()).isEmpty().toString())

            launch(Dispatchers.Main){
                if (stockDataBody!=null && stockInfoBrnd.isNotEmpty()){
                    appbarLay.visibility= View.VISIBLE
                    supportActionBar?.show()
                    ltpLay.visibility= View.VISIBLE
                    updationPane.visibility= View.VISIBLE
                    bgMain.visibility= View.VISIBLE
                    loadingTxt.visibility= View.GONE
                    mainContainer.setBackgroundColor(ContextCompat.getColor(this@stocksInfo, R.color.dash_bg))

                    toolbarTitle.text= stockInfoBrnd

                    predView.setOnClickListener {
                        val intent= Intent(this@stocksInfo, prediction::class.java)
                        intent.putExtra("symbolStock", inpSymbol)
                        intent.putExtra("ltp", stockDataBody.regularMarketPrice.toString())
                        startActivity(intent)
                        if (mInterstitialAd != null) {
                            mInterstitialAd?.show(this@stocksInfo)
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.")
                        }
                    }

                    stockLTP.text= stockDataBody.regularMarketPrice.toString()
                     val change: Float = (stockDataBody.regularMarketPrice-stockDataBody.previousClose)
                    stockChange.text= String.format("%.2f", change).toFloat().toString()
                    val currentTime = Date()
                    val formatter = SimpleDateFormat("HH:mm:ss")
                    val formattedTime = formatter.format(currentTime)
                    stockMarketTime.text= "Last Updated at: $formattedTime"

                    startPriceUpdateLoop()

                    withContext(Dispatchers.IO){
                        if (!mWatchlistModel.searchWatchlistsDB(inpSymbol.uppercase()).isEmpty()){
                            val data= mWatchlistModel.searchWatchlistsDB(inpSymbol.uppercase())
                            withContext(Dispatchers.Main){
                                watchlistViewTXT.text= "Watchlisted"
                                watchlistIcon.setImageResource(R.drawable.baseline_bookmark_remove_24)
                                watchlistView.setOnClickListener {
                                    if (wlBtnClickCount==0){
                                        mWatchlistModel.deleteUser(data)
                                        customToast.makeText(this@stocksInfo, "Successfully removed from Watchlists", 3).show()
                                        watchlistViewTXT.text= "Watchlist"
                                        watchlistIcon.setImageResource(R.drawable.baseline_bookmark_24)
                                        wlBtnClickCount++
                                    } else{
                                        customToast.makeText(this@stocksInfo, "Already removed from Watchlists", 2).show()
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main){
                                watchlistView.setOnClickListener {
                                    if (wlBtnClickCount==0){
                                        val stockData= watchlists(inpSymbol.uppercase(), stockInfoBrnd, stockDataBody.regularMarketPrice.toDouble(), changeStatus(change.toDouble()))
                                        mWatchlistModel.addWatchlists(stockData)
                                        // Toast.makeText(this@stocksInfo, "Successfully watchlisted!", Toast.LENGTH_SHORT).show()
                                        customToast.makeText(this@stocksInfo, "Successfully added to Watchlists", 1).show()
                                        watchlistViewTXT.text= "Watchlisted"
                                        watchlistIcon.setImageResource(R.drawable.baseline_bookmark_24)
                                        wlBtnClickCount++
                                    } else{
                                        customToast.makeText(this@stocksInfo, "Already added to Watchlists", 2).show()
                                    }

                                }
                            }
                        }
                    }

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

    private fun changeStatus(change: Double): String{
        if (change>0){
            return "POSITIVE"
        } else if (change<0){
            return "NEGATIVE"
        }
        return "NEUTRAL"
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

    private fun changePrevious(currentDay: Double, previousDay: Double): Double {
        return (currentDay-previousDay)
    }

    private fun previousCloses(t1: Double, t2: Double, t3: Double, t4: Double, t5: Double, t6: Double){
        findViewById<TextView>(R.id.trendPrice1).text= String.format("%.2f", t1)
        findViewById<TextView>(R.id.trendPrice2).text= String.format("%.2f", t2)
        findViewById<TextView>(R.id.trendPrice3).text= String.format("%.2f", t3)
        findViewById<TextView>(R.id.trendPrice4).text= String.format("%.2f", t4)
        findViewById<TextView>(R.id.trendPrice5).text= String.format("%.2f", t5)

        findViewById<TextView>(R.id.trendChange).text= changePrice(t1, t2)
        findViewById<TextView>(R.id.trendChange2).text= changePrice(t2, t3)
        findViewById<TextView>(R.id.trendChange3).text= changePrice(t3, t4)
        findViewById<TextView>(R.id.trendChange4).text= changePrice(t4, t5)
        findViewById<TextView>(R.id.trendChange5).text= changePrice(t5, t6)

        statusPrevious(t1, t2, R.id.trendTxt1)
        statusPrevious(t2, t3, R.id.trendTxt2)
        statusPrevious(t3, t4, R.id.trendTxt3)
        statusPrevious(t4, t5, R.id.trendTxt4)
        statusPrevious(t5, t6, R.id.trendTxt5)
    }

    private fun statusPrevious(t1: Double, t2: Double, id: Int){
        val id= findViewById<TextView>(id)
        val change= changePrice(t1, t2).toDouble()
        if (change>0){
            id.setTextColor(Color.GREEN)
            id.text= "POSITIVE"
        } else if (change<0){
            id.setTextColor(Color.RED)
            id.text= "NEGATIVE"
        } else{
            id.setTextColor(Color.GRAY)
            id.text=  "NEUTRAL"
        }
    }

    private fun changePrice(t1: Double, t2: Double): String{
        return String.format("%.2f", t1-t2)
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