package com.rohnsha.stocksense.indices_db

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.customToast
import com.rohnsha.stocksense.ltpAPI.object_ltp
import com.rohnsha.stocksense.watchlist_db.watchlists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.rohnsha.stocksense.StockDataResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.Exception

class indicesAdapter(private val application: Application): RecyclerView.Adapter<indicesAdapter.indicesViewHolder>(){

    private var indicesList= emptyList<indices>()
    private var mInterstitialAd: InterstitialAd? = null
    private val mIndicesVM: indicesViewModel= ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(indicesViewModel::class.java)
    private final var TAG = "watchlistsAD"


    class indicesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): indicesViewHolder {
        return indicesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.index_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return indicesList.size
    }

    override fun onBindViewHolder(holder: indicesViewHolder, position: Int) {
        val currentitem= indicesList[position]

        holder.itemView.apply {

            val handler= CoroutineExceptionHandler { _, throwable ->
                Log.e("exception", "Coroutine ended")
            }
            CoroutineScope(Dispatchers.IO + handler).launch{
                val updateLTP= launch {
                    while (true){
                        delay(1000L)
                        try {
                            Log.e("indicesReq", "sending requests (${currentitem.symbol})....")
                            val response= getStockData(currentitem.symbol)
                            Log.e("indicesReq", "updating table (${currentitem.symbol})....")
                            val stockChange = (response!!.regularMarketPrice-response!!.previousClose)
                            val updateData= indices(currentitem.symbol, currentitem.company, response!!.regularMarketPrice.toDouble(), changeToStatus(stockChange))
                            mIndicesVM.aupdateIndices(updateData)
                            Log.e("indicesReq", "updated table (${currentitem.symbol})....")
                            throw Exception("Finished WOrking")
                        } catch (e: Exception){
                            withContext(Dispatchers.Main){
                                Log.e("indicesError", "sending errors....")
                            }
                        }
                        throw Exception("Finished WOrking")
                    }
                }
                delay(1000L)
                updateLTP.cancel()
                updateLTP.join()
                Log.e("indicesReq", "cancelled....")
            }

            val green_custom= ContextCompat.getColor(context, R.color.green_custom)
            val red_custom= ContextCompat.getColor(context, R.color.red_custom)

            findViewById<TextView>(R.id.symbolTV).text= currentitem.symbol
            findViewById<TextView>(R.id.sName).text= currentitem.company
            findViewById<TextView>(R.id.rvLtp).text= String.format("%.2f", currentitem.ltp)
            findViewById<TextView>(R.id.rvStatus).text= currentitem.status.toString()
            findViewById<TextView>(R.id.logoInit).text= currentitem.company.substring(0, 1)
            if (currentitem.status=="POSITIVE"){
                findViewById<TextView>(R.id.rvStatus).setTextColor(green_custom)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(
                    green_custom)
            } else if (currentitem.status=="NEGATIVE"){
                findViewById<TextView>(R.id.rvStatus).setTextColor(red_custom)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(
                    red_custom)
            } else{
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.GRAY)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(
                    Color.GRAY)
            }
        }

    }

    fun setIndices(stocks: List<indices>){
        this.indicesList= stocks
        notifyDataSetChanged()
    }

    private fun changeToStatus(change: Float): String {
        if (change>0){
            return "POSITIVE"
        } else if (change<0){
            return "NEGATIVE"
        }
        return "NEUTRAL"
    }

    suspend fun getStockData(symbol: String): com.rohnsha.stocksense.Result.Meta?{
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