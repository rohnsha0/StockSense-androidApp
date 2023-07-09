package com.rohnsha.stocksense.watchlist_db

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.stocksInfo

class watchlistsAdapter: RecyclerView.Adapter<watchlistsAdapter.watchlistsViewHolder>() {

    private var watchlistsList= emptyList<watchlists>()

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

            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentitem.symbol)
                context.startActivity(intent)
            }
        }
    }

    fun setWatchlists(stocks: List<watchlists>){
        this.watchlistsList= stocks
        notifyDataSetChanged()
    }

}