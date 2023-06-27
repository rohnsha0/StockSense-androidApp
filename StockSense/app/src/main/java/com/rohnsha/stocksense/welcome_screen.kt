package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class welcome_screen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        val createACbtn= findViewById<Button>(R.id.createAccountBTN)
        val loginTV= findViewById<TextView>(R.id.loginTV)

        createACbtn.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }

        loginTV.setOnClickListener {
            startActivity(Intent(this, login::class.java))
        }
    }
}