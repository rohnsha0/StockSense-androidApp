package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class onBoard2 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board2)

        val onBoardBtn2= findViewById<Button>(R.id.oneBoard2Btn)

        onBoardBtn2.setOnClickListener {
            startActivity(Intent(this, onBoard3::class.java))
        }

    }
}