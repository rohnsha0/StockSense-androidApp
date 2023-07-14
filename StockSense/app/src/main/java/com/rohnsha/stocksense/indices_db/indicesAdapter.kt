package com.rohnsha.stocksense.indices_db

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.customToast
import com.rohnsha.stocksense.ltpAPI.object_ltp
import com.rohnsha.stocksense.watchlist_db.watchlists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class indicesAdapter(private val application: Application): RecyclerView.Adapter<indicesAdapter.indicesViewHolder>(){

    private var indicesList= emptyList<indices>()
    private var mInterstitialAd: InterstitialAd? = null
    private val mIndicesVM: indicesViewModel= ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(indicesViewModel::class.java)
    private final var TAG = "watchlistsAD"


    class indicesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): indicesViewHolder {
        return indicesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.index_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return indicesList.size
    }

    override fun onBindViewHolder(holder: indicesViewHolder, position: Int) {
        val currentitem= indicesList[position]

        holder.itemView.apply {

            GlobalScope.launch(Dispatchers.IO){
                val dynamicURL= "https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/ltp/${currentitem.symbol}"
                try {
                    val response= object_ltp.ltpAPIService.getLTP(dynamicURL)
                    val updateData= indices(currentitem.symbol, currentitem.company, response.ltp, response.change)
                    mIndicesVM.aupdateIndices(updateData)

                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        customToast.makeText(context, e.toString(), 2).show()
                    }
                }

            }

            findViewById<TextView>(R.id.symbolTV).text= currentitem.symbol
            findViewById<TextView>(R.id.sName).text= currentitem.company
            findViewById<TextView>(R.id.rvLtp).text= String.format("%.2f", currentitem.ltp)
            findViewById<TextView>(R.id.rvStatus).text= currentitem.status
            findViewById<TextView>(R.id.logoInit).text= currentitem.symbol.substring(0, 1)
            if (currentitem.status=="POSITIVE"){
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.GREEN)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(
                    Color.GREEN)
            } else if (currentitem.status=="NEGATIVE"){
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.RED)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(
                    Color.RED)
            } else{
                findViewById<TextView>(R.id.rvStatus).setTextColor(Color.GRAY)
                findViewById<ImageView>(R.id.logoHistory).backgroundTintList= ColorStateList.valueOf(
                    Color.GRAY)
            }
        }

    }

    fun setIndices(stocks: List<indices>){
        this.indicesList= stocks
        notifyDataSetChanged()
    }
}