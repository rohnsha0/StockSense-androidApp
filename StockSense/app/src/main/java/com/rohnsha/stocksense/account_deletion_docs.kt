package com.rohnsha.stocksense

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class account_deletion_docs : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_deletion_docs)


        findViewById<ImageView>(R.id.backAccountDel).setOnClickListener {
            onBackPressed()
        }

        findViewById<TextView>(R.id.AccDoc4).text= "Managing your account's security and privacy is essential. By following these procedures for account deletion and logout, you can maintain control over your personal information and ensure a secure and convenient experience on our platform or application.\n" +
                "\n" +
                "If you have any questions or encounter issues during the account deletion or logout process, please don't hesitate to contact our support team for assistance."

        findViewById<TextView>(R.id.AccDoc3).text= "Logging out of your account is a simple process:\n1. Click on your profile picture or username, usually located in the top-right corner of the application or website.\n" +
                "2. Look for the \"Logout\"\n" +
                "3. You will be logged out, and your session will be terminated. You will need to re-enter your credentials to log in again."

        findViewById<TextView>(R.id.AccDoc2).text= "Upon account deletion, your data will be removed in accordance with our data retention policy. This may include:\n" +
                "\n" +
                "Personal information (e.g., name, email, phone number)\n" +
                "Please note that some data, such as anonymized usage statistics, may be retained for analytical purposes but will not be associated with your deleted account."
        findViewById<TextView>(R.id.accDoc1).text= "This documentation outlines the procedures for users to delete their accounts and logout from our platform or application. Account deletion and logout are important features that empower users to manage their privacy and security on our platform. By following these guidelines, we ensure a smooth and secure process for our users."
    }
}