package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.appbar.AppBarLayout
import com.rohnsha.stocksense.database.pred_glance_db.glance_view_model
import com.rohnsha.stocksense.database.pred_glance_db.pred_glance
import com.rohnsha.stocksense.api.prediction_api.pred_object.predAPIservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class prediction : AppCompatActivity() {
    lateinit var symbol: String
    lateinit var stockLTP: String
    lateinit var company: String
    var modelStr by Delegates.notNull<Float>()
    var modelStrHR by Delegates.notNull<Float>()
    private lateinit var mPredictinViewModel: glance_view_model
    private var isPresentInGlance by Delegates.notNull<Boolean>()
    private lateinit var glanceEntry: List<pred_glance>
    private lateinit var predDate: TextView
    private lateinit var predTime: TextView

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
        val data_range= findViewById<TextView>(R.id.modelDateVal)
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
        val timeHorizon= findViewById<Button>(R.id.timeHorizonSwitch)
        val predictedDate= findViewById<TextView>(R.id.predictedDateVal)
        var is1Dpred=true
        var needToSendReq1H= true

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
                        predictedDateTime()
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

            timeHorizon.setOnClickListener {
                val trendTV= findViewById<TextView>(R.id.highVal)
                val remarksTV= findViewById<TextView>(R.id.lowVal)
                val unavailableText= this@prediction.getString(R.string.not_available)
                val closeAnimation= findViewById<LottieAnimationView>(R.id.closeAnim)
                val trendAnimation= findViewById<LottieAnimationView>(R.id.trendAnim)
                val remarkAnimation= findViewById<LottieAnimationView>(R.id.remarkAnim)
                if (is1Dpred){
                    timeHorizon.text= "1D Prediction"
                    timeHorizon.isEnabled= false
                    predTitle.text= "Hourly Predictions"
                    lifecycleScope.launch {
                        if (needToSendReq1H){
                            withContext(Dispatchers.Main){
                                closeAnimation.visibility= View.VISIBLE
                                trendAnimation.visibility= View.VISIBLE
                                remarkAnimation.visibility= View.VISIBLE
                            }
                            val dynamicURLhourly= "https://quuicqg435fkhjzpkawkhg4exi0vjslb.lambda-url.ap-south-1.on.aws/prediction/hr/$symbol"
                            try {
                                val responseHR= predAPIservice.getModelData(dynamicURLhourly)
                                modelStrHR= responseHR.predicted_close
                                withContext(Dispatchers.Main){
                                    closeAnimation.visibility= View.GONE
                                    trendAnimation.visibility= View.GONE
                                    remarkAnimation.visibility= View.GONE
                                    val predCloseValHR= String.format("%.2f", modelStrHR).toFloat()
                                    predClose.text= predCloseValHR.toString()
                                    data_range.text= "Sep 2021 - Sep 2023"
                                    trendRemarks(predCloseValHR, stockLTP.toFloat())
                                }
                                needToSendReq1H= false
                                predictedDateTime(is1Dactive = false)
                            } catch (e: Exception){
                                Log.d("hrException", e.toString())
                                closeAnimation.visibility= View.GONE
                                trendAnimation.visibility= View.GONE
                                remarkAnimation.visibility= View.GONE
                                predClose.text= unavailableText
                                predClose.setTypeface(null, Typeface.ITALIC)
                                remarksTV.text= unavailableText
                                remarksTV.setTypeface(null, Typeface.ITALIC)
                                trendTV.text= unavailableText
                                trendTV.setTypeface(null, Typeface.ITALIC)
                            }
                        } else {
                            try {
                                val predCloseValueHR= String.format("%.2f", modelStrHR).toFloat()
                                predClose.text= predCloseValueHR.toString()
                                trendRemarks(predCloseValueHR, stockLTP.toFloat())
                                data_range.text= "Sep 2021 - Sep 2023"
                                predictedDateTime(is1Dactive = false)
                            } catch (e: Exception){
                                Log.d("hrException", e.toString())
                                predClose.text= unavailableText
                                predClose.setTypeface(null, Typeface.ITALIC)
                                remarksTV.text= unavailableText
                                remarksTV.setTypeface(null, Typeface.ITALIC)
                                trendTV.text= unavailableText
                                trendTV.setTypeface(null, Typeface.ITALIC)
                            }
                        }
                    }
                    is1Dpred= false
                    timeHorizon.isEnabled= true
                } else{
                    timeHorizon.text= "1H Prediction"
                    predTitle.text= "Day Predictions"
                    val predCloseValue= String.format("%.2f", modelStr).toFloat()
                    data_range.text= "2002 - January 26, 2023"
                    predClose.text= predCloseValue.toString()
                    trendRemarks(predCloseValue, stockLTP.toFloat())
                    predictedDateTime(is1Dactive = true)
                    is1Dpred= true
                }
            }

            dashBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    val dbCount= mPredictinViewModel.getDBcount()
                    Log.d("existsGlance", mPredictinViewModel.searchDBGlance(symbol).isEmpty().toString())
                    if (dbCount>=1){
                        val addedSymbol= mPredictinViewModel.queryGlance().symbol
                        if (!isPresentInGlance){
                            withContext(Dispatchers.Main){
                                customToast.makeText(this@prediction, "Remove ${addedSymbol.substringBefore('.')} to add this to Dashboard", 2).show()
                            }
                        } else {
                            if (touchCountGlance==0){
                                withContext(Dispatchers.Main){
                                    mPredictinViewModel.deleteGlance(glanceEntry)
                                    customToast.makeText(this@prediction, "Successfully removed from Dashboard", 1).show()
                                    dashPred.setImageResource(R.drawable.baseline_leak_add_24)
                                    touchCountGlance++
                                }
                            } else if (touchCountGlance>0){
                                withContext(Dispatchers.Main){
                                    customToast.makeText(this@prediction, "Already removed from Dashboard", 2).show()
                                }
                            }
                        }
                    } else if (dbCount==0){
                        val prediction_details= pred_glance(symbol, company, stockLTP.toDouble(), modelStr.toDouble(), remarks = findViewById<TextView>(R.id.lowVal).text.toString(),
                            trend = findViewById<TextView>(R.id.highVal).text as String
                        )
                        mPredictinViewModel.addGlance(prediction_details)
                        launch(Dispatchers.Main){
                            customToast.makeText(this@prediction, "Successfully added to Dashboard", 1).show()
                            dashPred.setImageResource(R.drawable.baseline_leak_remove_24)
                        }

                    }
                }
            }
        }
    }

    private fun systemDate(): String {
        val dateFormat= SimpleDateFormat("MMM d, yyyy", Locale.US)
        val currentDate= Date(System.currentTimeMillis())
        return dateFormat.format(currentDate)
    }

    private fun predictedDateTime(is1Dactive: Boolean= true){
        predTime= findViewById(R.id.predictedTimeVal)
        predDate= findViewById(R.id.predictedDateVal)
        val currentTime = Calendar.getInstance().time

        if (is1Dactive){
            Log.d("checkHoliday", isHoliday(Date()).toString())
            predTime.text= formattedTime(thresholdTime(15, 15))
            checkDayStatus()
        } else {
            val current_day= SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

            if (isHoliday(Date()) || current_day== "Saturday" || current_day=="Sunday"){
                predTime.text= formattedTime(thresholdTime(9,15))
            } else {
                if (currentTime.after(thresholdTime(0,0)) && currentTime.before(thresholdTime(9,15))){
                    predTime.text= formattedTime(thresholdTime(9,15))
                } else if (currentTime.after(thresholdTime(15,15)) && currentTime.before(thresholdTime(23,59))){
                    predTime.text= formattedTime(thresholdTime(9,15))
                    predDate.text= formattedDate(1)
                } else if (currentTime.after(thresholdTime(9,15)) && currentTime.before(thresholdTime(10,15))){
                    predTime.text= formattedTime(thresholdTime(10,15))
                } else if (currentTime.after(thresholdTime(10,15)) && currentTime.before(thresholdTime(11,15))){
                    predTime.text= formattedTime(thresholdTime(11,15))
                } else if (currentTime.after(thresholdTime(11,15)) && currentTime.before(thresholdTime(12,15))){
                    predTime.text= formattedTime(thresholdTime(12,15))
                } else if (currentTime.after(thresholdTime(12,15)) && currentTime.before(thresholdTime(13,15))){
                    predTime.text= formattedTime(thresholdTime(13,15))
                } else if (currentTime.after(thresholdTime(13,15)) && currentTime.before(thresholdTime(14,15))){
                    predTime.text= formattedTime(thresholdTime(14,15))
                } else if (currentTime.after(thresholdTime(14,15)) && currentTime.before(thresholdTime(15,15))){
                    predTime.text= formattedTime(thresholdTime(15,15))
                }
            }
        }
    }

    private fun checkDayStatus(){
        val current_day= SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
        Log.d("day", current_day)
        if (current_day=="Saturday"){
            predDate.text= formattedDate(2)
        } else if (current_day=="Sunday"){
            predDate.text= formattedDate(1)
        } else if (isHoliday(Date())){
            predDate.text= formattedDate(1)
        } else {
            predDate.text= SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        }
    }

    private fun thresholdTime(hour:  Int, min: Int): Date {
        val thresholdCal= Calendar.getInstance()
        thresholdCal.apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
        }
        return thresholdCal.time
    }

    private fun formattedTime(time: Date): String {
        val timeFormat = DateFormat.getTimeFormat(applicationContext)
        return timeFormat.format(time)
    }

    private fun formattedDate(dateIntrimented: Int): String {
        val calenderInst= Calendar.getInstance()
        calenderInst.time= Date()
        calenderInst.add(Calendar.DAY_OF_YEAR, dateIntrimented)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val twoDaysAfter = dateFormat.format(calenderInst.time)
        return twoDaysAfter
    }

    private fun isHoliday(date: Date): Boolean {
        val holidayDates = listOf(
            "Jan 26, 2023",
            "Mar 07, 2023",
            "Mar 30, 2023",
            "Apr 04, 2023",
            "Apr 07, 2023",
            "Apr 14, 2023",
            "May 01, 2023",
            "Jun 29, 2023",
            "Aug 15, 2023",
            "Sep 19, 2023",
            "Oct 02, 2023",
            "Oct 24, 2023",
            "Nov 14, 2023",
            "Nov 27, 2023",
            "Dec 25, 2023",
        )
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(date)
        Log.d("day", dateStr)

        return dateStr in holidayDates
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