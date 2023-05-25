package com.example.stocksense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchView= findViewById<SearchView>(R.id.searchClick)

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

                val url = "https://query1.finance.yahoo.com/v8/finance/chart/$inputSymbol"

                GlobalScope.launch(Dispatchers.IO) {
                    val response = makeRequest(url)
                    launch(Dispatchers.Main) {
                        if (response != null) {
                            val json = response.string()
                            handleResponse(json, inputSymbol)
                        } else {
                            navigateToErrorPage()
                        }
                    }
                }

            } else {
                Toast.makeText(this@MainActivity, "Enter a valid input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeRequest(url: String): ResponseBody? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body
    }

    private fun handleResponse(json: String, inputSymbol: String) {
        val gson = Gson()
        val stockDataResponse = gson.fromJson(json, StockDataResponse::class.java)
        val result = stockDataResponse.chart.result

        if (result != null && result.isNotEmpty()) {
            val stockData = result.first().meta
            navigateToStocksInfo(stockData, inputSymbol)
        } else {
            navigateToErrorPage()
        }
    }

    private fun navigateToStocksInfo(stockData: Result.Meta, symbol: String) {
        val intent = Intent(this, stocksInfo::class.java)
        intent.putExtra("symbol", symbol)
        startActivity(intent)
    }

    private fun navigateToErrorPage() {
        val intent = Intent(this, errorPage::class.java)
        startActivity(intent)
    }
}