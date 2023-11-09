package com.rohnsha.stocksense.database.search_history

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.stocksInfo

class searchHistoryAdapter(private val application: Application): RecyclerView.Adapter<searchHistoryAdapter.searchHistoryViewHolder>() {

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

            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentSearchItem.search_history.toString())
                context.startActivity(intent)
            }

            findViewById<ConstraintLayout>(R.id.searchLay).setOnLongClickListener {
                Log.d("longClick", "long clicked at ${currentSearchItem.search_history.toString()}")

                val deleteSheet= bottomSheetSearchDelete(
                    id_search = currentSearchItem.id,
                    search_symbol = currentSearchItem.search_history
                )
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