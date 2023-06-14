package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth= FirebaseAuth.getInstance()

        val signUpTXT= findViewById<TextView>(R.id.signUpTxt)
        val loginBTN= findViewById<Button>(R.id.loginBTN)

        loginBTN.setOnClickListener {
            Toast.makeText(this, "Logging you in...", Toast.LENGTH_LONG).show()
            loginUser()
        }

        signUpTXT.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }
    }

    private fun loginUser(){
        val email= findViewById<EditText>(R.id.etMail).text.toString()
        val password= findViewById<EditText>(R.id.etPass).text.toString()

        if (checkEmail() && checkPass()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInStatus()
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@login, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this@login, "Either Email or Pass is having issues", Toast.LENGTH_LONG)
        }
    }

    private fun checkLoggedInStatus(){
        if (auth.currentUser==null){
            Toast.makeText(this@login, "Something went wrong, try again!", Toast.LENGTH_LONG)
        } else {
            startActivity(Intent(this@login, homepage::class.java))
            finish()
        }
    }

    private fun checkEmail(): Boolean {
        val email= findViewById<EditText>(R.id.etMail).text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this@login, "Email is needed to proceed forward", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("^\\w+[.-]?\\w+@\\w+([.-]?\\w+)+\$").matches(email)) {
            Toast.makeText(this@login, "Email should be in the specified format", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPass(): Boolean {
        val password= findViewById<EditText>(R.id.etPass).text.toString()
        if (password.isEmpty()){
            Toast.makeText(this@login, "Password can't be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isNotEmpty() && password.length<8){
            Toast.makeText(this@login, "Password should be minimum 8 characters long", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}