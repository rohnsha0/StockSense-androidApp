package com.rohnsha.stocksense.database.search_history

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.stocksInfo
import android.content.Context

class searchHistoryAdapter: RecyclerView.Adapter<searchHistoryAdapter.searchHistoryViewHolder>() {

    private var searchHistoryList= emptyList<search_history>()

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
            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentSearchItem.search_history.toString().uppercase()+".NS")
                context.startActivity(intent)
            }
        }
    }

    fun setSearchHistory(history: List<search_history>){
        this.searchHistoryList= history
        notifyDataSetChanged()
    }

}