package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.appbar.AppBarLayout
import com.rohnsha.stocksense.pred_glance_db.glance_view_model
import com.rohnsha.stocksense.pred_glance_db.pred_glance
import com.rohnsha.stocksense.prediction_api.pred_object.predAPIservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class prediction : AppCompatActivity() {
    lateinit var symbol: String
    lateinit var stockLTP: String
    lateinit var company: String
    var modelStr by Delegates.notNull<Float>()
    lateinit var mBannerAdView: AdView
    lateinit var mBannerAdView2: AdView
    private lateinit var mPredictinViewModel: glance_view_model
    private var isPresentInGlance by Delegates.notNull<Boolean>()
    private lateinit var glanceEntry: List<pred_glance>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        symbol= intent.getStringExtra("symbolStock").toString()
        stockLTP= intent.getStringExtra("ltp").toString()
        company= intent.getStringExtra("company").toString()

        Log.d("companyName", company)

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
        val backBtnPred= findViewById<ImageView>(R.id.backPred)
        mBannerAdView= findViewById(R.id.bannerAdPred)
        mBannerAdView2= findViewById(R.id.bannerAdPred2)
        val dashBtn= findViewById<ImageView>(R.id.dashPred)
        mPredictinViewModel= ViewModelProvider(this)[glance_view_model::class.java]

        backBtnPred.setOnClickListener {
            onBackPressed()
        }

        predIntent.setOnClickListener {
            val intent= Intent(this, homepage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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

        lifecycleScope.launch(Dispatchers.IO){
            val dynnamicURL= "https://quuicqg435fkhjzpkawkhg4exi0vjslb.lambda-url.ap-south-1.on.aws/prediction/$symbol"

            try {
                Log.e("predReq", "sending request")
                val response= predAPIservice.getModelData(dynnamicURL)
                Log.e("predReq", "sent request")
                modelStr= response.predicted_close
                Log.d("responseServer", modelStr.toString())

                withContext(Dispatchers.Main){
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
                }
            } catch (e:Exception){
                withContext(Dispatchers.Main){
                    loadingView.visibility= View.GONE
                    errorView.visibility= View.VISIBLE
                    Log.e("error", "something went wrong")
                }
            }

            Log.d("getDBcount", mPredictinViewModel.getDBcount().toString())

            val dashPred= findViewById<ImageView>(R.id.dashPred)

            lifecycleScope.launch(Dispatchers.IO){
                glanceEntry= mPredictinViewModel.searchDBGlance(symbol)
                isPresentInGlance= glanceEntry.isNotEmpty()
                if (isPresentInGlance){
                    withContext(Dispatchers.Main){
                        dashPred.setImageResource(R.drawable.baseline_leak_remove_24)
                    }
                }
            }
            var touchCountGlance= 0

            dashBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    val dbCount= mPredictinViewModel.getDBcount()
                    Log.d("existsGlance", mPredictinViewModel.searchDBGlance(symbol).isEmpty().toString())
                    if (dbCount>=1){
                        val addedSymbol= mPredictinViewModel.queryGlance().symbol
                        if (!isPresentInGlance){
                            withContext(Dispatchers.Main){
                                customToast.makeText(this@prediction, "Remove ${addedSymbol.substringBefore('.')} to add this to Glance Dash", 2).show()
                            }
                        } else {
                            if (touchCountGlance==0){
                                withContext(Dispatchers.Main){
                                    mPredictinViewModel.deleteGlance(glanceEntry)
                                    customToast.makeText(this@prediction, "Successfully removed from Glance Dash", 1).show()
                                    dashPred.setImageResource(R.drawable.baseline_leak_add_24)
                                    touchCountGlance++
                                }
                            } else if (touchCountGlance>0){
                                withContext(Dispatchers.Main){
                                    customToast.makeText(this@prediction, "Already removed from Glance Dash", 2).show()
                                }
                            }
                        }
                    } else if (dbCount==0){
                        val prediction_details= pred_glance(symbol, company, stockLTP.toDouble(), modelStr.toDouble(), remarks = findViewById<TextView>(R.id.lowVal).text.toString(),
                            trend = findViewById<TextView>(R.id.highVal).text as String
                        )
                        mPredictinViewModel.addGlance(prediction_details)
                        launch(Dispatchers.Main){
                            customToast.makeText(this@prediction, "Successfully added to Glance", 1).show()
                            dashPred.setImageResource(R.drawable.baseline_leak_remove_24)
                        }

                    }
                }
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
            remarks.text= "Bearish stock performance"
        } else {
            trend.text= "Neutral"
            remarks.text= "Not a strong upward force, stock is performing as expected"
        }
    }
}