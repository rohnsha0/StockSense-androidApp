package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class logout_account : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout_account)

        auth= FirebaseAuth.getInstance()

        val containerInfo= findViewById<ConstraintLayout>(R.id.deleteInfoContainer)
        val viewDelete= findViewById<ConstraintLayout>(R.id.viewDelete)
        val deletionLayout= findViewById<ConstraintLayout>(R.id.deletion)
        val deleteBtn= findViewById<Button>(R.id.deleteBtn)
        val logoutBtn= findViewById<ConstraintLayout>(R.id.viewLogout)
        val backBtn= findViewById<ImageView>(R.id.backDlt)

        backBtn.setOnClickListener {
            onBackPressed()
        }

        viewDelete.setOnClickListener {
            containerInfo.visibility= View.GONE
            deletionLayout.visibility= View.VISIBLE
        }

        checkLoginState()

        deleteBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                try {
                    auth.currentUser!!.delete().await()
                    withContext(Dispatchers.Main){
                        checkLoginState()
                    }
                } catch (e: Exception){
                    customToast.makeText(this@logout_account, e.toString(), 2).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                auth.signOut()
                withContext(Dispatchers.Main){
                    if (checkLoginState()){

                    }
                }
            }
        }
    }

    private fun checkLoginState(): Boolean {
        if (auth.currentUser==null){
            customToast.makeText(this, "Successfully applied actions!", 1).show()
            val intent= Intent(this, welcome_screen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return true
        }
        return false
    }
}