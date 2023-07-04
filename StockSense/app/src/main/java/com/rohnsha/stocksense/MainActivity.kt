package com.rohnsha.stocksense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic("app_info")
        setContentView(R.layout.activity_main)

        val searchView= findViewById<SearchView>(R.id.searchClickFrag)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    private fun performSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            Toast.makeText(this@MainActivity, "Field can't be null", Toast.LENGTH_SHORT).show()
        } else {
            if (query.matches(Regex("[a-zA-Z]+"))) {
                val inputSymbol = "$query.NS"

                val intent = Intent(this, stocksInfo::class.java)
                intent.putExtra("symbol", inputSymbol)
                startActivity(intent)
            } else {
                Toast.makeText(this@MainActivity, "Enter a valid input", Toast.LENGTH_SHORT).show()
            }
        }
    }
}