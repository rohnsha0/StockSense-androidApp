package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.rohnsha.stocksense.databinding.ActivityHomepageBinding
import com.rohnsha.stocksense.databinding.ActivityMainBinding

class homepage : AppCompatActivity(), home.FragmentChangeListener {

    private lateinit var binding: ActivityHomepageBinding
    private lateinit var analytics: FirebaseAnalytics

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        analytics= FirebaseAnalytics.getInstance(this)
        replaceFragment(home())

        val homeFrag= supportFragmentManager.findFragmentById(R.id.main_content) as? home
        homeFrag?.view?.findViewById<TextView>(R.id.viewWl)?.setOnClickListener {
            replaceFragment(watchListFRAG())
        }

        binding.bottomNav.background= null

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeNav -> replaceFragment(home())
                R.id.searchNav -> replaceFragment(searchFragment())
                R.id.watchList -> replaceFragment(watchListFRAG())
                R.id.more -> replaceFragment(moreFragment())

                else ->{

                }
            }
            true
        }

        val FABSearch = binding.searchBTNNav

        FABSearch.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_content, fragment)
        fragmentTransaction.commit()
    }

    override fun replaceFrag(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_content, fragment)
        fragmentTransaction.commit()
        binding.bottomNav.menu.findItem(R.id.watchList)?.isChecked= true
    }
}
