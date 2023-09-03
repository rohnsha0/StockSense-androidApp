package com.rohnsha.stocksense.database.search_history

import android.app.Activity
import android.app.Application
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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class searchHistoryAdapter(private val application: Application): RecyclerView.Adapter<searchHistoryAdapter.searchHistoryViewHolder>() {

    private var searchHistoryList= emptyList<search_history>()
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "searchHistoryAD"

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
            findViewById<TextView>(R.id.tvHistory).text= currentSearchItem.search_history?.substringBefore('.')?.lowercase()

            val index= findViewById<TextView>(R.id.tvIndexSearch)
            index.visibility= View.VISIBLE
            val indexSuffix= currentSearchItem.search_history?.substringAfter('.')
            if (indexSuffix=="NS"){
                index.text= "in NSE"
            } else if (indexSuffix== "BO"){
                index.text= "in BSE"
            }

            //findViewById<TextView>(R.id.textSearchIcom).text= currentSearchItem.search_history?.substring(0, 1)

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
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }
                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                }
                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }
                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }

            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentSearchItem.search_history.toString())
                context.startActivity(intent)
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(context as Activity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }

            findViewById<ConstraintLayout>(R.id.searchLay).setOnLongClickListener {
                Log.d("longClick", "long clicked at ${currentSearchItem.search_history.toString()}")

                val deleteSheet= bottomSheetSearchDelete(id_search = currentSearchItem.id, search_symbol = currentSearchItem.search_history)
                deleteSheet.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, deleteSheet.tag)

                true
            }
        }
    }

    fun setSearchHistory(history: List<search_history>){
        this.searchHistoryList= history
        notifyDataSetChanged()
    }

}