package com.rohnsha.stocksense.database.watchlist_db

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.stocksInfo

class watchlistAdapterFive: RecyclerView.Adapter<watchlistsAdapter.watchlistsViewHolder>() {

    private var watchlistsList= emptyList<watchlists>()

    class watchlistsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): watchlistsAdapter.watchlistsViewHolder {
        return watchlistsAdapter.watchlistsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.index_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return minOf(watchlistsList.size, 3)
    }

    override fun onBindViewHolder(holder: watchlistsAdapter.watchlistsViewHolder, position: Int) {
        val currentitem= watchlistsList[position]
        holder.itemView.apply {

            findViewById<TextView>(R.id.symbolTV).text = currentitem.symbol.substringBefore('.')
            findViewById<TextView>(R.id.sName).text = currentitem.company.limitText(20)
            findViewById<TextView>(R.id.rvLtp).text = String.format("%.2f", currentitem.ltp)
            findViewById<TextView>(R.id.rvStatus).text = currentitem.status
            findViewById<TextView>(R.id.logoInit).text = currentitem.symbol.substring(0, 1)

            val imgIndex= findViewById<ImageView>(R.id.stockLogoIndex)
            imgIndex.visibility= View.VISIBLE
            val stockIndex= currentitem.symbol.substringAfter('.')
            if (stockIndex=="NS"){
                imgIndex.setImageResource(R.drawable.nse_logo)
            } else if (stockIndex=="BO"){
                imgIndex.setImageResource(R.drawable.bse_logo)
            }

            setOnClickListener {
                val intent = Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentitem.symbol)
                context.startActivity(intent)
            }
        }
    }

    fun setWatchlists(stocks: List<watchlists>){
        this.watchlistsList= stocks.subList(0, minOf(stocks.size, 3))
        notifyDataSetChanged()
    }

}