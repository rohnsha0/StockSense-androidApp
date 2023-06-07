package com.rohnsha.stocksense

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

class prediction : AppCompatActivity() {
    lateinit var symbol: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        symbol= intent.getStringExtra("symbolStock").toString()
        symbol= symbol.uppercase()

        val predClose= findViewById<TextView>(R.id.closeVal)
        val mainContainenr= findViewById<LinearLayout>(R.id.mainContent)
        val topBar= findViewById<LinearLayout>(R.id.titleBlock)
        val iconPred= findViewById<ImageView>(R.id.iconPredSVG)
        val iconPredBG= findViewById<ImageView>(R.id.blueCircle)
        val loadingView= findViewById<ConstraintLayout>(R.id.loadingContainer)
        val errorView= findViewById<ConstraintLayout>(R.id.errorViewPred)

        GlobalScope.launch(Dispatchers.Main){
            val dynnamicURL= "https://web-production-c587.up.railway.app/query/$symbol"

            try {
                val response= predAPIservice.getModelData(dynnamicURL)
                val modelStr= response.predicted_close
                Log.d("responseServer", modelStr.toString())

                if (modelStr.toDouble() != 0.0){
                    mainContainenr.visibility= View.VISIBLE
                    topBar.visibility= View.VISIBLE
                    iconPred.visibility= View.VISIBLE
                    iconPredBG.visibility= View.VISIBLE
                    loadingView.visibility= View.GONE
                    val predCloseVal= String.format("%.2f", modelStr).toFloat()
                    predClose.text= predCloseVal.toString()
                    Log.e("response", modelStr.toString())
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
}