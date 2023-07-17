package com.rohnsha.stocksense.docs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.rohnsha.stocksense.R

class searchbar_docs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchbar_docs)
        val back= findViewById<ImageView>(R.id.backPred)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}