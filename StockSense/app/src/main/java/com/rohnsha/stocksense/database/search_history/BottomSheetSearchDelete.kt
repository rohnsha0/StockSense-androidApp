package com.rohnsha.stocksense.database.search_history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.database.watchlist_db.watchlists
import com.rohnsha.stocksense.database.watchlist_db.watchlistsVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class bottomSheetSearchDelete(
    val id_search: Int,
    val search_symbol: String?,
    val isSearch: Boolean = true,
    val brandName: String? = null,
    val wlData: List<watchlists>? = null,
    val isPresent: Boolean= false
) : BottomSheetDialogFragment() {

    private lateinit var mSearchVM: search_history_model
    private lateinit var mWatchlistModel: watchlistsVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_history_delete, container, false)

        mSearchVM= ViewModelProvider(this)[search_history_model::class.java]
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]
        val searchSymbol= view.findViewById<TextView>(R.id.tvStockStmbolDelete)
        val actionBtn= view.findViewById<TextView>(R.id.cancelRemoveSearch)
        val h1= view.findViewById<TextView>(R.id.removeSearchH1)
        val warning1= view.findViewById<TextView>(R.id.selectedDlt)
        val deleteWarning= view.findViewById<TextView>(R.id.dltWarn)

        searchSymbol.text= search_symbol?.substringBefore('.')

        if (isSearch){
            actionBtn.setOnClickListener {
                mSearchVM.deleteSearch(search_history(id = id_search, search_history = search_symbol))
                dismiss()
            }
        } else {
            if (isPresent){
                Log.d("wlData", "item found it watchlist")
                h1.text= "Remove Watchlist"
                deleteWarning.text= "Are you sure you want to delete this entry from your watchlists?"
                actionBtn.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO){
                        if (wlData != null) {
                            mWatchlistModel.deleteUser(wlData)
                        }
                        Log.d("wlData", "item deleted in watchlist")
                    }
                    dismiss()
                }
            } else {
                h1.text= "Add Watchlist"
                warning1.text= "Selected Item to be added: "
                Log.d("wlData", "item not found it watchlist")
                deleteWarning.text= "Are you sure you want to add this entry to your watchlist?"
                val wlDataAdd= watchlists(search_symbol.toString(), brandName.toString(), 0.0, "NEUTRAL")
                actionBtn.text= "Add"
                actionBtn.setOnClickListener {
                    Log.d("wlData", "item clicked it watchlist")
                    lifecycleScope.launch(Dispatchers.IO){
                        mWatchlistModel.addWatchlists(wlDataAdd)
                    }
                    dismiss()
                }
            }
        }

        return view
    }

}