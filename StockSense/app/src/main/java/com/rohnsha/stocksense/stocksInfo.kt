package com.rohnsha.stocksense
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.db.williamchart.view.LineChartView
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.rohnsha.stocksense.stock_infoAPI.dataclass_stocksInfo
import com.rohnsha.stocksense.stock_infoAPI.object_stockInfo.stocksInfoAPIservice
import com.rohnsha.stocksense.watchlist_db.watchlists
import com.rohnsha.stocksense.watchlist_db.watchlistsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates


data class StockDataResponse(
    val chart: Chart
)

data class Chart(
    val result: List<Result>
)

data class Result(
    val meta: Meta,
    val timestamp: List<Long>,
    val indicators: Indicators
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
data class Indicators(
    val quote: List<Quote>
)

data class Quote(
    val close: List<Double>
)

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
    lateinit var stockInfoBrnd: String
    private lateinit var mWatchlistModel: watchlistsVM
    private lateinit var chartSet: List<Pair<String, Float>>
    private var isPresentWL by Delegates.notNull<Boolean>()
    private lateinit var stocksInfoResponse: dataclass_stocksInfo


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
        val mainContainer= findViewById<ConstraintLayout>(R.id.scrollContainer)
        val toolbar= findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarDash)
        val toolbarTitle= findViewById<TextView>(R.id.dashTitle)
        val appbarLay= findViewById<AppBarLayout>(R.id.appbarLayDash)
        val watchlistViewTXT= findViewById<TextView>(R.id.btnWatchlist)
        val watchlistIcon= findViewById<ImageView>(R.id.watchlistIcon)
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]
        val lineChart= findViewById<LineChartView>(R.id.lineChart)
        val backStockInfo= findViewById<ImageView>(R.id.backStockInfo)
        val allData= findViewById<TextView>(R.id.textView2)

        allData.setOnClickListener {
            customToast.makeText(this, "Resources unavailable", 2).show()
        }

        tecchView.setOnClickListener {
            val intent= Intent(this, stock_data::class.java)
            intent.putExtra("symbolStock", inpSymbol)
            intent.putExtra("nameStock", stockInfoBrnd)
            startActivity(intent)
        }

        backStockInfo.setOnClickListener {
            onBackPressed()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.hide()

        backBTN.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val stockDataFtcher= stockDataFetcher()
        lifecycleScope.launch(Dispatchers.IO){
            val stockDataBody= stockDataFtcher.getStockData(inpSymbol)
            val dynamicUrl= "https://quuicqg435fkhjzpkawkhg4exi0vjslb.lambda-url.ap-south-1.on.aws/query/v3/${inpSymbol.uppercase()}"
            try {
                Log.e("stockInfo", "sending requests....")
                stocksInfoResponse= stocksInfoAPIservice.getStockInfo(dynamicUrl)
                Log.e("stockInfo", "request sent....")
                withContext(Dispatchers.Main){
                    stockInfoBrnd= stocksInfoResponse.company
                    previousCloses(
                        stocksInfoResponse.t1,
                        stocksInfoResponse.d1,
                        stocksInfoResponse.t2,
                        stocksInfoResponse.d2,
                        stocksInfoResponse.t3,
                        stocksInfoResponse.d3,
                        stocksInfoResponse.t4,
                        stocksInfoResponse.d4,
                        stocksInfoResponse.t5,
                        stocksInfoResponse.d5,
                        stocksInfoResponse.t6,
                        stocksInfoResponse.d6
                    )
                    chartSet= listOf(
                        "T-6" to stocksInfoResponse.t6.toFloat(),
                        "T-5" to stocksInfoResponse.t5.toFloat(),
                        "T-4" to stocksInfoResponse.t4.toFloat(),
                        "T-3" to stocksInfoResponse.t3.toFloat(),
                        "T-2" to stocksInfoResponse.t2.toFloat(),
                        "T-1" to stocksInfoResponse.t1.toFloat(),
                    )
                    lineChart.animation.duration= 1000L
                }
            } catch (e: Exception){
                stockInfoBrnd= inpSymbol.substringBefore('.').uppercase()
                chartSet= listOf(
                    "x" to 0F,
                    "y" to 0F,
                    "z" to 0F
                )
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
                    lineChart.visibility= View.VISIBLE
                    mainContainer.setBackgroundColor(ContextCompat.getColor(this@stocksInfo, R.color.dash_bg))
                    lineChart.animate(chartSet)

                    toolbarTitle.text= stockInfoBrnd

                    predView.setOnClickListener {
                        val intent= Intent(this@stocksInfo, prediction::class.java)
                        intent.putExtra("symbolStock", inpSymbol)
                        intent.putExtra("ltp", stockDataBody.regularMarketPrice.toString())
                        Log.d("companyName", stockInfoBrnd)
                        intent.putExtra("company", stockInfoBrnd)
                        startActivity(intent)
                    }

                    stockLTP.text= stockDataBody.regularMarketPrice.toString()
                     val change: Float = (stockDataBody.regularMarketPrice-stockDataBody.previousClose)
                    mainContainer.setBackgroundColor(checkColor(change.toDouble()))
                    stockChange.text= String.format("%.2f", change).toFloat().toString()
                    val currentTime = Date()
                    val formatter = SimpleDateFormat("HH:mm:ss")
                    val formattedTime = formatter.format(currentTime)
                    stockMarketTime.text= "Last Updated at: $formattedTime"

                    setAdditionStockInfo(
                        change.toDouble(),
                        inpSymbol,
                        dayHigh = stocksInfoResponse.dayHigh,
                        dayLow = stocksInfoResponse.dayLow,
                        isin = stocksInfoResponse.isin,
                        W52High = stocksInfoResponse.W52High,
                        W52wLow = stocksInfoResponse.W52Low,
                        sector = stocksInfoResponse.indusry,
                        fv = stocksInfoResponse.face_value
                    )
                    startPriceUpdateLoop()

                    withContext(Dispatchers.IO){
                        isPresentWL= mWatchlistModel.searchWatchlistsDB(inpSymbol.uppercase()).isNotEmpty()
                        if (isPresentWL){
                            watchlistViewTXT.text= "Watchlisted"
                            watchlistIcon.setImageResource(R.drawable.baseline_bookmark_remove_24)
                        }
                    }

                    watchlistView.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO){
                            val data= mWatchlistModel.searchWatchlistsDB(inpSymbol.uppercase())
                            withContext(Dispatchers.Main){
                                if (isPresentWL){
                                    mWatchlistModel.deleteUser(data)
                                    customToast.makeText(this@stocksInfo, "Successfully removed from Watchlists", 3).show()
                                    watchlistViewTXT.text= "Watchlist"
                                    watchlistIcon.setImageResource(R.drawable.baseline_bookmark_add_24)
                                } else {
                                    withContext(Dispatchers.IO){
                                        val stockData= watchlists(inpSymbol.uppercase(), stockInfoBrnd, stockDataBody.regularMarketPrice.toDouble(), changeStatus(change.toDouble()))
                                        mWatchlistModel.addWatchlists(stockData)
                                    }
                                    customToast.makeText(this@stocksInfo, "Successfully added to Watchlists", 1).show()
                                    watchlistViewTXT.text= "Watchlisted"
                                    watchlistIcon.setImageResource(R.drawable.baseline_bookmark_remove_24)
                                }
                            }
                            isPresentWL= mWatchlistModel.searchWatchlistsDB(inpSymbol.uppercase()).isNotEmpty()
                        }
                    }

                    updateLTP.setOnClickListener {
                        updateStockPrice(inpSymbol)
                    }

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

    private fun changePrevious(currentDay: Double, previousDay: Double): Double {
        return (currentDay-previousDay)
    }

    private fun previousCloses(
        t1: Double,
        d1: String,
        t2: Double,
        d2: String,
        t3: Double,
        d3: String,
        t4: Double,
        d4: String,
        t5: Double,
        d5: String,
        t6: Double,
        d6: String
    ){
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

        findViewById<TextView>(R.id.trendDate1).text= d1
        findViewById<TextView>(R.id.trendDate2).text= d2
        findViewById<TextView>(R.id.trendDate3).text= d3
        findViewById<TextView>(R.id.trendDate4).text= d4
        findViewById<TextView>(R.id.trendDate5).text= d5

        statusPrevious(t1, t2, R.id.trendTxt1)
        statusPrevious(t2, t3, R.id.trendTxt2)
        statusPrevious(t3, t4, R.id.trendTxt3)
        statusPrevious(t4, t5, R.id.trendTxt4)
        statusPrevious(t5, t6, R.id.trendTxt5)

        changeIconColor(changePrice(t1, t2).toDouble(), R.id.trendIco1)
        changeIconColor(changePrice(t2, t3).toDouble(), R.id.trendIco2)
        changeIconColor(changePrice(t3, t4).toDouble(), R.id.trendIco3)
        changeIconColor(changePrice(t4, t5).toDouble(), R.id.trendIco4)
        changeIconColor(changePrice(t5, t6).toDouble(), R.id.trendIco5)
    }

    private fun setAdditionStockInfo(
        change: Double,
        symbolInp: String,
        W52High: Double,
        W52wLow: Double,
        isin: String,
        fv: Int,
        sector: String,
        dayHigh: Double,
        dayLow: Double
    ){
        val stockInforImg= findViewById<ImageView>(R.id.imageStockInfo)
        val stockInfoStatus= findViewById<TextView>(R.id.stockInfoStatus)
        val symbol= findViewById<TextView>(R.id.symbolTvStockInfo)
        val indexTradedIn= findViewById<TextView>(R.id.symbolIndex)
        symbol.text= symbolInp.substringBefore('.')
        indexTradedIn.text= getIndex(symbolInp)

        findViewById<TextView>(R.id.ISINtv).text= isin
        findViewById<TextView>(R.id.fv).text= fv.toString()
        findViewById<TextView>(R.id.sectorTVtxt).text= sector
        findViewById<TextView>(R.id.W52Range).text= "${String.format("%.1f", W52High)} - ${String.format("%.1f", W52wLow)}"
        findViewById<TextView>(R.id.dayRangeTv).text= "${String.format("%.1f", dayHigh)} - ${String.format("%.1f", dayLow)}"

        val green_custom= ContextCompat.getColor(this, R.color.green_custom)
        val red_custom= ContextCompat.getColor(this, R.color.red_custom)

        stockInfoStatus.text= checkStatusText(change)

        if (change>0){
            stockInforImg.backgroundTintList= ColorStateList.valueOf(green_custom)
            stockInfoStatus.setTextColor(green_custom)
        } else if (change<0){
            stockInforImg.backgroundTintList= ColorStateList.valueOf(red_custom)
            stockInfoStatus.setTextColor(red_custom)
        } else {
            stockInforImg.backgroundTintList= ColorStateList.valueOf(Color.GRAY)
            stockInfoStatus.setTextColor(Color.GRAY)
        }
    }

    private fun getIndex(symbol: String): String {
        val symbolCheck= symbol.substringAfter('.')
        val indexTradedIn= findViewById<TextView>(R.id.symbolIndex)
        if (symbolCheck=="NS"){
            return "in NSE"
        } else if (symbolCheck=="BO"){
            return "in BSE"
        }
        return "unidentified"
    }

    private fun checkStatusText(change: Double): String {
        if (change>0){
            return "POSITIVE"
        } else if (change<0){
            return "NEGATIVE"
        }
        return "NEUTRAL"
    }

    private fun changeIconColor(change: Double, id: Int){
        val green_custom= ContextCompat.getColor(this, R.color.green_custom)
        val red_custom= ContextCompat.getColor(this, R.color.red_custom)
        val id= findViewById<ImageView>(id)
        if (change>0){
            id.backgroundTintList= ColorStateList.valueOf(green_custom)
            id.setImageResource(R.drawable.baseline_add_24)
        } else if (change<0){
            id.backgroundTintList= ColorStateList.valueOf(red_custom)
            id.setImageResource(R.drawable.baseline_remove_24)
        } else {
            id.backgroundTintList= ColorStateList.valueOf(Color.GRAY)
            id.setImageResource(R.drawable.baseline_tag_24)
        }
    }

    private fun statusPrevious(t1: Double, t2: Double, id: Int){
        val id= findViewById<TextView>(id)
        val green_custom= ContextCompat.getColor(this, R.color.green_custom)
        val red_custom= ContextCompat.getColor(this, R.color.red_custom)
        val change= changePrice(t1, t2).toDouble()
        if (change>0){
            id.setTextColor(green_custom)
            id.text= "POSITIVE"
        } else if (change<0){
            id.setTextColor(red_custom)
            id.text= "NEGATIVE"
        } else{
            id.setTextColor(Color.GRAY)
            id.text=  "NEUTRAL"
        }
    }

    private fun changePrice(t1: Double, t2: Double): String{
        return String.format("%.2f", t1-t2)
    }

    private fun checkColor(change: Double): Int {
        if (change>0){
            return ContextCompat.getColor(this, R.color.green_custom)
        } else if (change<0){
            return ContextCompat.getColor(this, R.color.red_custom)
        }
        return ContextCompat.getColor(this, R.color.dash_bg)
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