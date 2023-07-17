package com.rohnsha.stocksense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class help_support : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        val back= findViewById<ImageView>(R.id.backHelp)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}