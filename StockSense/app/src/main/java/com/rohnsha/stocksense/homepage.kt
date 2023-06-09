package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class homepage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val buttonSearch= findViewById<Button>(R.id.button2search)
        val FABSearch= findViewById<FloatingActionButton>(R.id.searchBTNNav)

        buttonSearch.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        FABSearch.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}