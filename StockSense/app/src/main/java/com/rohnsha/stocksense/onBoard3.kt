package com.rohnsha.stocksense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class onBoard3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board3)

        val onBoard3Btn= findViewById<Button>(R.id.oneBoard3Btn)

        onBoard3Btn.setOnClickListener {
            startActivity(Intent(this, onBoard4::class.java))
        }

    }
}