package com.rohnsha.stocksense.watchlist_db

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.stocksInfo

class watchlistsAdapter: RecyclerView.Adapter<watchlistsAdapter.watchlistsViewHolder>() {

    private var watchlistsList= emptyList<watchlists>()
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "watchlistsAD"

    class watchlistsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): watchlistsViewHolder {
        return watchlistsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.index_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return watchlistsList.size
    }

    override fun onBindViewHolder(holder: watchlistsViewHolder, position: Int) {
        val currentitem= watchlistsList[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.symbolTV).text= currentitem.symbol
            findViewById<TextView>(R.id.sName).text= currentitem.company
            findViewById<TextView>(R.id.rvLtp).text= String.format("%.2f", currentitem.ltp)
            findViewById<TextView>(R.id.rvStatus).text= currentitem.status
            findViewById<TextView>(R.id.logoInit).text= currentitem.symbol.substring(0, 1)
            if (currentitem.status=="POSITIVE"){
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.GREEN)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(Color.GREEN)
            } else if (currentitem.status=="NEGATIVE"){
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.RED)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(Color.RED)
            } else{
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.GRAY)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(Color.GRAY)
            }

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
                intent.putExtra("symbol", currentitem.symbol)
                context.startActivity(intent)
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(context as Activity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }
        }
    }

    fun setWatchlists(stocks: List<watchlists>){
        this.watchlistsList= stocks
        notifyDataSetChanged()
    }

}