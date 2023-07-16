package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.rohnsha.stocksense.indices_db.indices
import com.rohnsha.stocksense.indices_db.indicesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class onBoard4 : AppCompatActivity() {

    private lateinit var mIndicesViewModel: indicesViewModel


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board4)

        val onBoard4Btn= findViewById<Button>(R.id.oneBoard4Btn)
        mIndicesViewModel= ViewModelProvider(this)[indicesViewModel::class.java]

        GlobalScope.launch(Dispatchers.IO){
            val indexNiftyData= indices("^NSEI", "Nifty 50", 0.0, "NEUTRAL")
            mIndicesViewModel.addIndices(indexNiftyData)
            val indexSensexData= indices("^BSESN", "S&P BSE SENSEX", 0.0, "NEUTRAL")
            mIndicesViewModel.addIndices(indexSensexData)
        }

        onBoard4Btn.setOnClickListener {
            val sharedPreferences: SharedPreferences = getSharedPreferences("loginKey", Context.MODE_PRIVATE)
            val editor= sharedPreferences.edit()
            editor.putBoolean("viewedOnBoarding", true).apply()

            val intent= Intent(this, welcome_screen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}