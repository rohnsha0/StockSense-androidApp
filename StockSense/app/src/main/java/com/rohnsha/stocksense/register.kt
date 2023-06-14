package com.rohnsha.stocksense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class register : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBTN= findViewById<Button>(R.id.signUpBTN)

        auth= FirebaseAuth.getInstance()

        registerBTN.setOnClickListener {
            Toast.makeText(this, "Registering User...", Toast.LENGTH_LONG).show()
            registerUser()
        }

    }

    private fun registerUser(){
        val email= findViewById<EditText>(R.id.etMailTxt).text.toString()
        val password= findViewById<EditText>(R.id.etPass).text.toString()
        val firstName= findViewById<EditText>(R.id.editTextText).text.toString()
        val lastName= findViewById<EditText>(R.id.etLastName).text.toString()

        if (checkEmail() && checkPass() && checkLastFirstName()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    auth.currentUser?.let {
                        val userprofile= UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()
                        it.updateProfile(userprofile).await()
                    }
                    withContext(Dispatchers.Main){
                        checkLoggedInStatus()
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@register, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInStatus(){
        if (auth.currentUser==null){
            Toast.makeText(this@register, "Something went wrong, try again!", Toast.LENGTH_LONG).show()
        } else {
            startActivity(Intent(this@register, homepage::class.java))
            finish()
        }
    }

    private fun checkEmail(): Boolean {
        val email= findViewById<EditText>(R.id.etMailTxt).text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this@register, "Email is needed to proceed forward", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("^\\w+[.-]?\\w+@\\w+([.-]?\\w+)+\$").matches(email)) {
            Toast.makeText(this@register, "Email should be in the specified format", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPass(): Boolean {
        val password= findViewById<EditText>(R.id.etPass).text.toString()
        if (password.isEmpty()){
            Toast.makeText(this@register, "Password can't be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isNotEmpty() && password.length<8){
            Toast.makeText(this@register, "Password should be minimum 8 characters long", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun checkLastFirstName(): Boolean{
        val firstName= findViewById<EditText>(R.id.editTextText).text.toString()
        val lastName= findViewById<EditText>(R.id.etLastName).text.toString()

        if (firstName.isEmpty()){
            Toast.makeText(this@register, "First name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (lastName.isEmpty()){
            Toast.makeText(this@register, "Last name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("[a-zA-Z]+").matches(firstName)){
            Toast.makeText(this@register, "First name can only contain alphabets", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("[a-zA-Z]+").matches(lastName)){
            Toast.makeText(this@register, "Last name can only contain alphabets", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}