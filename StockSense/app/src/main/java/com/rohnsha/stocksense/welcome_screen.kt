package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class welcome_screen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        val createACbtn= findViewById<Button>(R.id.createAccountBTN)
        val loginTV= findViewById<TextView>(R.id.loginTV)

        GlobalScope.launch(Dispatchers.IO){
            val sharedPreferences: SharedPreferences= getSharedPreferences("loginKey", Context.MODE_PRIVATE)
            val editor= sharedPreferences.edit()
            if (!sharedPreferences.getBoolean("viewedOnBoarding", false)){
                val intent= Intent(this@welcome_screen, onBoarding1::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        createACbtn.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }

        loginTV.setOnClickListener {
            startActivity(Intent(this, login::class.java))
        }
    }
}