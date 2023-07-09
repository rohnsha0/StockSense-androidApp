package com.rohnsha.stocksense.database.search_history

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.stocksInfo
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class searchHistoryAdapter: RecyclerView.Adapter<searchHistoryAdapter.searchHistoryViewHolder>() {

    private var searchHistoryList= emptyList<search_history>()
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"

    class searchHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchHistoryViewHolder {
        return searchHistoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_search_history, parent, false))
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    override fun onBindViewHolder(holder: searchHistoryViewHolder, position: Int) {
        val currentSearchItem= searchHistoryList[position]

        holder.itemView.apply {
            findViewById<TextView>(R.id.tvHistory).text= currentSearchItem.search_history

            var adRequest = AdRequest.Builder().build()
            val adID= context.getString(R.string.interstitialID)

            InterstitialAd.load(context,adID, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.toString()?.let { Log.d(TAG, it) }
                    mInterstitialAd = null
                    InterstitialAd.load(context,adID, adRequest, object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            adError?.toString()?.let { Log.d(TAG, it) }
                            mInterstitialAd = null
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Log.d(TAG, "Ad was loaded.")
                            mInterstitialAd = interstitialAd
                        }
                    })
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })

            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentSearchItem.search_history.toString().uppercase()+".NS")
                context.startActivity(intent)
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(context as Activity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }
        }
    }

    fun setSearchHistory(history: List<search_history>){
        this.searchHistoryList= history
        notifyDataSetChanged()
    }

}