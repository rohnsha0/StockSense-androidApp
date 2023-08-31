package com.rohnsha.stocksense.database.search_history.search_suggestions

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.database.search_history.search_history
import com.rohnsha.stocksense.database.search_history.search_history_model
import com.rohnsha.stocksense.indices_db.indicesViewModel
import com.rohnsha.stocksense.searchFragment
import com.rohnsha.stocksense.stocksInfo

class stocksSearchAdapter(private val application: Application): RecyclerView.Adapter<stocksSearchAdapter.stockSearchViewHolder>() {

    private var stockLists= emptyList<search>()
    private val mHistoryViewModel: search_history_model = ViewModelProvider.AndroidViewModelFactory
        .getInstance(application)
        .create(
        search_history_model::class.java
    )


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
            findViewById<TextView>(R.id.tvHistory).text= currentStock.company
            findViewById<ImageView>(R.id.logoHistory).setImageResource(R.drawable.baseline_search_24)
            val index= findViewById<TextView>(R.id.tvIndexSearch)
            val indexSuffix= currentStock.symbol.substringAfter('.')
            if (indexSuffix=="NS"){
                index.text= "from NSE"
            } else if (indexSuffix== "BO"){
                index.text= "from BSE"
            }
            index.visibility= View.VISIBLE

            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", currentStock.symbol.uppercase())
                context.startActivity(intent)
                val queryDB= search_history(0, currentStock.symbol)
                mHistoryViewModel.addHistory(queryDB)
            }

        }
    }

    fun setStocksList(stock: List<search>){
        this.stockLists= stock
        notifyDataSetChanged()
    }

}