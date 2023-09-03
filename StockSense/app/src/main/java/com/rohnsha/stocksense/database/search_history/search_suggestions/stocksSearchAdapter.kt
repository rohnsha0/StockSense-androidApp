package com.rohnsha.stocksense.database.search_history.search_suggestions

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.database.search_history.search_history
import com.rohnsha.stocksense.database.search_history.search_history_model
import com.rohnsha.stocksense.indices_db.indicesViewModel
import com.rohnsha.stocksense.searchFragment
import com.rohnsha.stocksense.stocksInfo
import com.rohnsha.stocksense.watchlist_db.limitText

class stocksSearchAdapter(private val application: Application): RecyclerView.Adapter<stocksSearchAdapter.stockSearchViewHolder>() {

    private var stockLists= emptyList<search>()
    private val mHistoryViewModel: search_history_model = ViewModelProvider.AndroidViewModelFactory
        .getInstance(application)
        .create(
        search_history_model::class.java
    )
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "stockSearchAD"

    class stockSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): stockSearchViewHolder {
        return stockSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_search_history, parent, false))
    }

    override fun getItemCount(): Int {
        return stockLists.size
    }

    override fun onBindViewHolder(holder: stockSearchViewHolder, position: Int) {
        val currentStock= stockLists[position]

        holder.itemView.apply {
            val density = resources.displayMetrics.density
            val paddingImgView= (8 * density).toInt()

            findViewById<TextView>(R.id.tvHistory).text= currentStock.company
            findViewById<ImageView>(R.id.logoHistory).setImageResource(R.drawable.baseline_troubleshoot_24)
            findViewById<ImageView>(R.id.logoHistory).setPadding(paddingImgView)
            findViewById<TextView>(R.id.tvIndexSearch).visibility= View.GONE

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
                intent.putExtra("symbol", currentStock.yFinanceSymbol.uppercase())
                context.startActivity(intent)
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(context as Activity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                val queryDB= search_history(0, currentStock.yFinanceSymbol)
                mHistoryViewModel.addHistory(queryDB)
            }

        }
    }

    fun setStocksList(stock: List<search>){
        this.stockLists= stock
        notifyDataSetChanged()
    }

}