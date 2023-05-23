package com.example.stocksense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val inputSearch= findViewById<EditText>(R.id.inpSearch)
        val btnSearch= findViewById<Button>(R.id.btnfetch)
        var inputSymbol= ""

        btnSearch.setOnClickListener {
            inputSymbol= inputSearch.text.toString()
            if (inputSymbol== ""){
                Toast.makeText(
                    this@MainActivity,
                    "Field can't be null",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (inputSymbol.matches(Regex("[a-zA-Z]+"))){
                    inputSymbol= "${inputSearch.text.toString()}.NS"

                    val intent= Intent(this, stocksInfo::class.java)
                    intent.putExtra("symbol", inputSymbol)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Enter a valid input",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}