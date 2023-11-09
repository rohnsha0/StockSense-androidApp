package com.rohnsha.stocksense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rohnsha.stocksense.databinding.ActivityStockDataBinding
import com.rohnsha.stocksense.technical_api.object_technical.technicalAPIservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class stock_data : AppCompatActivity() {

    private lateinit var bindingData: ActivityStockDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingData= ActivityStockDataBinding.inflate(layoutInflater)
        setContentView(bindingData.root)

        val symbol= intent.getStringExtra("symbolStock").toString().uppercase()
        val name=intent.getStringExtra("nameStock").toString()

        lifecycleScope.launch(Dispatchers.IO){
            Log.e("stockData", "sending requests....")
            val dynamicURL= "https://quuicqg435fkhjzpkawkhg4exi0vjslb.lambda-url.ap-south-1.on.aws/technical/$symbol"
            Log.e("stockData", "sent requests....")
            try {
                val response= technicalAPIservice.getTehnicalData(dynamicURL)
                withContext(Dispatchers.Main){
                    bindingData.appbarData.visibility= View.VISIBLE
                    bindingData.blueCircle.visibility= View.VISIBLE
                    bindingData.iconInfoSVG.visibility= View.VISIBLE
                    bindingData.mainContent.visibility= View.VISIBLE
                    bindingData.predTitle.visibility= View.VISIBLE
                    bindingData.loadingContainerData.visibility= View.GONE

                    bindingData.dateVal.text= systemDate()
                    bindingData.symbolVal.text= symbol.substringBefore('.')
                    bindingData.SMA50Val.text= String.format("%.2f", response.sma50)
                    bindingData.SMA100Val.text= String.format("%.2f", response.sma100)
                    bindingData.SMA200Val.text= String.format("%.2f", response.sma200)
                    bindingData.EMA50Val.text= String.format("%.2f", response.ema50)
                    bindingData.EMA100Val.text= String.format("%.2f", response.ema100)
                    bindingData.EMA200Val.text= String.format("%.2f", response.ema200)
                    bindingData.RSIVal.text= String.format("%.2f", response.rsi)
                    bindingData.MACDVal.text= String.format("%.2f", response.macd)
                    bindingData.ATRval.text= String.format("%.2f", response.atr)
                    bindingData.BBUpperVal.text= String.format("%.2f", response.bollingerBandUpper)
                    bindingData.BBLowerVal.text= String.format("%.2f", response.bollingerBankLoweer)

                    bindingData.backData.setOnClickListener {
                        onBackPressed()
                    }

                    bindingData.dataIntentBTN.setOnClickListener {
                        val intent= Intent(this@stock_data, homepage::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            } catch (e:Exception){
                withContext(Dispatchers.Main){
                    bindingData.errorViewData.visibility= View.VISIBLE
                    bindingData.loadingContainerData.visibility= View.GONE
                }
                Log.e("error", e.toString())
            }
        }

    }

    private fun systemDate(): String {
        val dateFormat= SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val currentDate= Date(System.currentTimeMillis())
        return dateFormat.format(currentDate)
    }

}