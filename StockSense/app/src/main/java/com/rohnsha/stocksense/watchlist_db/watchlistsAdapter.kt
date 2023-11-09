package com.rohnsha.stocksense.watchlist_db

import android.content.Intent
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

            findViewById<TextView>(R.id.symbolTV).text= currentitem.symbol.substringBefore('.')
            findViewById<TextView>(R.id.sName).text= currentitem.company.limitText(20)
            findViewById<TextView>(R.id.rvLtp).text= String.format("%.2f", currentitem.ltp)
            findViewById<TextView>(R.id.rvStatus).text= currentitem.status
            findViewById<TextView>(R.id.logoInit).text= currentitem.symbol.substring(0, 1)

            val imgIndex= findViewById<ImageView>(R.id.stockLogoIndex)
            imgIndex.visibility= View.VISIBLE
            val stockIndex= currentitem.symbol.substringAfter('.')
            if (stockIndex=="NS"){
                imgIndex.setImageResource(R.drawable.nse_logo)
            } else if (stockIndex=="BO"){
                imgIndex.setImageResource(R.drawable.bse_logo)
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

fun String.limitText(maxChar: Int): String {
    return if (this.length <= maxChar) {
        this
    } else {
        this.take(maxChar) + "..."
    }
}