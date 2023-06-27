package com.rohnsha.stocksense

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.rvLTPobject.ltpAPIservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import watchlistDC

class watchlistAdapter(
    var watchlists: List<watchlistDC>
) : RecyclerView.Adapter<watchlistAdapter.WatchlistViewHolder>() {

    private val dataCache= HashMap<String, Double>()
    inner class WatchlistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.index_layout, parent, false)
        return WatchlistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return watchlists.size
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        holder.itemView.apply {

            val symbols= watchlists[position].symbol

            findViewById<TextView>(R.id.symbolTV).text= watchlists[position].symbol
            findViewById<TextView>(R.id.sName).text= watchlists[position].name

            val cachedData= dataCache[symbols]
            if (cachedData!=null){
                findViewById<TextView>(R.id.rvLtp).text= cachedData.toString()
            } else {
                findViewById<TextView>(R.id.rvLtp).text= "0.0"
            }

            GlobalScope.launch(Dispatchers.Main){
                val dynamicURL= "https://mn46q6wbitsmvcyyqqzyslocl40awcrz.lambda-url.ap-south-1.on.aws/ltp/$symbols"
                Log.d("symbol", symbols)

                try {
                    val response= ltpAPIservice.getLTPrv(dynamicURL)
                    findViewById<TextView>(R.id.rvLtp).text= response.ltp.toString()
                    dataCache[symbols]= response.ltp
                } catch (e: Exception){
                    Log.d("rvError", e.toString())
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            setOnClickListener {
                val intent= Intent(context, stocksInfo::class.java)
                intent.putExtra("symbol", watchlists[position].symbol)
                context.startActivity(intent)
            }
        }
    }


}