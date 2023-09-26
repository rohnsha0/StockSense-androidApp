package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        val forgot_pass= findViewById<TextView>(R.id.forgot_email)

        forgot_pass.setOnClickListener {
            val viewPassReset: View= layoutInflater.inflate(R.layout.reset_password, null)
            val dialogReset= BottomSheetDialog(this)
            dialogReset.setContentView(viewPassReset)

            val send_mail= viewPassReset.findViewById<TextView>(R.id.resetEmailBtn)
            val reset_email= viewPassReset.findViewById<EditText>(R.id.resetEmailEt)

            send_mail.setOnClickListener {
                if (!reset_email.text.isNullOrBlank() || !reset_email.text.isNullOrEmpty()){
                    if (isEmailValid(reset_email.text.toString())){
                        auth.sendPasswordResetEmail(reset_email.text.toString())
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    customToast.makeText(this, "Email sent successfully! Check mail!", 1).show()
                                    dialogReset.dismiss()
                                } else {
                                    customToast.makeText(this, "Password reset mail failed to be sent! Try again!", 2).show()
                                }
                            }
                    } else {
                        customToast.makeText(this, "Enter a valid email id!", 2).show()
                    }
                } else{
                    customToast.makeText(this, "Field cannot be empty!", 2).show()
                }
            }

            dialogReset.show()
        }

        loginBTN.setOnClickListener {
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
            lifecycleScope.launch(Dispatchers.IO) {
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
            customToast.makeText(this@login, "Either Email or Pass is having issues", 2).show()
        }
    }

    private fun checkLoggedInStatus(){
        if (auth.currentUser==null){
            Toast.makeText(this@login, "Something went wrong, try again!", Toast.LENGTH_LONG)
        } else {
            val intent= Intent(this, homepage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun checkEmail(): Boolean {
        val email= findViewById<EditText>(R.id.etMail).text.toString()
        if (email.isEmpty()) {
            customToast.makeText(this@login, "Email is needed to proceed forward", 2).show()
            return false
        } else if (!Regex("^\\w+[.-]?\\w+@\\w+([.-]?\\w+)+\$").matches(email)) {
            customToast.makeText(this@login, "Email should be in the specified format", 2).show()
            return false
        }
        return true
    }

    fun isEmailValid(email: String): Boolean {
        val regexPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return regexPattern.matches(email)
    }


    private fun checkPass(): Boolean {
        val password= findViewById<EditText>(R.id.etPass).text.toString()
        if (password.isEmpty()){
            customToast.makeText(this@login, "Password can't be empty", 2).show()
            return false
        } else if (password.isNotEmpty() && password.length<8){
            customToast.makeText(this@login, "Password should be minimum 8 characters long", 2).show()
        }
        return true
    }
}