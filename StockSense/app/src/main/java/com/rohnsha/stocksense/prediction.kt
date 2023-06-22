package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.rohnsha.stocksense.ml.ITCxNS
import com.rohnsha.stocksense.pred_object.predAPIservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class prediction : AppCompatActivity() {
    lateinit var symbol: String
    lateinit var stockLTP: String
    lateinit var mBannerAdView: AdView
    lateinit var mBannerAdView2: AdView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        symbol= intent.getStringExtra("symbolStock").toString()
        stockLTP= intent.getStringExtra("ltp").toString()
        symbol= symbol.uppercase()

        val predClose= findViewById<TextView>(R.id.closeVal)
        val mainContainenr= findViewById<LinearLayout>(R.id.mainContent)
        val topBar= findViewById<LinearLayout>(R.id.titleBlock)
        val iconPred= findViewById<ImageView>(R.id.iconPredSVG)
        val iconPredBG= findViewById<ImageView>(R.id.blueCircle)
        val loadingView= findViewById<ConstraintLayout>(R.id.loadingContainer)
        val errorView= findViewById<ConstraintLayout>(R.id.errorViewPred)
        val toolbar= findViewById<Toolbar>(R.id.toolBarPred)
        val predIntent= findViewById<Button>(R.id.predIntentBTN)
        val appbarLay= findViewById<AppBarLayout>(R.id.appbarPred)
        val predTitle= findViewById<TextView>(R.id.predTitle)
        val dateValue= findViewById<TextView>(R.id.dateVal)
        val symbolStock= findViewById<TextView>(R.id.stockSymbolVal)
        val stockLTPInfo= findViewById<TextView>(R.id.currentPriceValue)
        mBannerAdView= findViewById(R.id.bannerAdPred)
        mBannerAdView2= findViewById(R.id.bannerAdPred2)

        predIntent.setOnClickListener {
            startActivity(Intent(this, homepage::class.java))
        }

        var adClickCount = 0

        val adRequest= AdRequest.Builder().build()
        mBannerAdView.loadAd(adRequest)
        mBannerAdView2.loadAd(adRequest)
        mBannerAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                adClickCount= clickProcess(adClickCount)
            }
            override fun onAdFailedToLoad(adError : LoadAdError) {
                super.onAdFailedToLoad(adError)
                mBannerAdView.loadAd(adRequest)
            }
            override fun onAdLoaded() {
                if (checkAdStatus()){
                    mBannerAdView.visibility= View.VISIBLE
                }else{
                    mBannerAdView.visibility= View.GONE
                }
            }
        }

        mBannerAdView2.adListener = object: AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                adClickCount= clickProcess(adClickCount)
            }
            override fun onAdFailedToLoad(adError : LoadAdError) {
                super.onAdFailedToLoad(adError)
                mBannerAdView2.loadAd(adRequest)
            }
            override fun onAdLoaded() {
                if (checkAdStatus()){
                    mBannerAdView2.visibility= View.VISIBLE
                } else{
                    mBannerAdView2.visibility= View.GONE
                }
            }
        }

        setSupportActionBar(toolbar)

        GlobalScope.launch(Dispatchers.Main){
            val dynnamicURL= "https://web-production-c587.up.railway.app/query/$symbol"

            try {
                val response= predAPIservice.getModelData(dynnamicURL)
                val modelStr= response.predicted_close
                Log.d("responseServer", modelStr.toString())

                if (modelStr.toDouble() != 0.0){
                    appbarLay.visibility= View.VISIBLE
                    mainContainenr.visibility= View.VISIBLE
                    // topBar.visibility= View.VISIBLE
                    iconPred.visibility= View.VISIBLE
                    iconPredBG.visibility= View.VISIBLE
                    predTitle.visibility= View.VISIBLE
                    loadingView.visibility= View.GONE
                    dateValue.text= systemDate()
                    val predCloseVal= String.format("%.2f", modelStr).toFloat()
                    predClose.text= predCloseVal.toString()
                    stockLTPInfo.text= stockLTP
                    symbolStock.text= symbol.substringBefore('.')
                    trendRemarks(predCloseVal, stockLTP.toFloat())
                    Log.e("response", modelStr.toString())
                    Log.d("resultsMinus", (stockLTP.toFloat()- predCloseVal).toString())
                } else {
                    loadingView.visibility= View.GONE
                    errorView.visibility= View.VISIBLE
                }
            } catch (e:Exception){
                loadingView.visibility= View.GONE
                errorView.visibility= View.VISIBLE
                Log.e("error", "something went wrong")
            }
        }
    }

    private fun updateLastAdClickTimeMillis(currentTimeMillis: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val adReEnableTimeMillis = 3 * 60 * 60 * 1000
        val sharedPreferences = this.getSharedPreferences("AdClickPredBanner", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("lastAdClickTimeMillis", currentTimeMillis)
        editor.apply()

        val lastAdClickTimeMillis = sharedPreferences.getLong("lastAdClickTimeMillis", 0)
        if (currentTimeMillis - lastAdClickTimeMillis > adReEnableTimeMillis) {
            mBannerAdView.visibility = View.VISIBLE
            mBannerAdView2.visibility= View.VISIBLE
        }
    }

    private fun getLastAdClickTimeMillis(): Long {
        val sharedPreferences = this.getSharedPreferences("AdClickPredBanner", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("lastAdClickTimeMillis", 0)
    }

    private fun systemDate(): String {
        val dateFormat= SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val currentDate= Date(System.currentTimeMillis())
        return dateFormat.format(currentDate)
    }

    private fun clickProcess(clickCount: Int): Int{
        var adClickCount= clickCount
        val adClickTimeLimitMillis = 2 * 60 * 60 * 1000
        adClickCount++
        if (adClickCount >= 5) {
            val currentTimeMillis = System.currentTimeMillis()
            val lastAdClickTimeMillis = getLastAdClickTimeMillis()
            if (currentTimeMillis - lastAdClickTimeMillis <= adClickTimeLimitMillis) {
                mBannerAdView.visibility = View.GONE
                mBannerAdView2.visibility= View.GONE
                Toast.makeText(this@prediction, "You're abusing app usage policy. It might lead to account suspension!", Toast.LENGTH_LONG).show()
                adClickCount= 0
            }
        }
        updateLastAdClickTimeMillis(System.currentTimeMillis())
        return adClickCount
    }

    private fun checkAdStatus(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val adReEnableTimeMillis = 3 * 60 * 60 * 1000
        val sharedPreferences = this.getSharedPreferences("AdClickPredBanner", Context.MODE_PRIVATE)

        val lastAdClickTimeMillis = sharedPreferences.getLong("lastAdClickTimeMillis", 0)
        if (currentTimeMillis - lastAdClickTimeMillis < adReEnableTimeMillis) {
            return false
        }
        return true
    }

    private fun trendRemarks(predictPrice:Float, ltp:Float){
        val trend= findViewById<TextView>(R.id.highVal)
        val remarks= findViewById<TextView>(R.id.lowVal)
        val priceDifference= ltp-predictPrice
        if (priceDifference>0){
            trend.text= "Uptrend"
            remarks.text= "Stock outperforming predictions"
        } else if (priceDifference<0){
            trend.text= "Downtrend"
            remarks.text= "Bullish stock performance"
        } else {
            trend.text= "Neutral"
            remarks.text= "Not a strong upward force stock is performing as expected"
        }
    }
}