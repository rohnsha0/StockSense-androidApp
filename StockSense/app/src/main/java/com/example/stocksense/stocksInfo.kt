package com.example.stocksense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class stocksInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_info)

        val inpSymbol= intent.getStringExtra("symbol")
        val stockName= findViewById<TextView>(R.id.tvName)

        stockName.text= inpSymbol.toString()
    }
}