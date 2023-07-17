package com.rohnsha.stocksense.docs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.rohnsha.stocksense.R

class data_docs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_docs)
        val back= findViewById<ImageView>(R.id.backData)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}