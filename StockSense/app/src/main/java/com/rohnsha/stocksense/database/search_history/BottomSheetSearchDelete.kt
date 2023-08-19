package com.rohnsha.stocksense.database.search_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohnsha.stocksense.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class bottomSheetSearchDelete(
    val id_search: Int,
    val search_symbol: String?,
) : BottomSheetDialogFragment() {

    private lateinit var mSearchVM: search_history_model

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_history_delete, container, false)

        mSearchVM= ViewModelProvider(this)[search_history_model::class.java]

        view.findViewById<TextView>(R.id.tvStockStmbolDelete).text= search_symbol.toString()

        view.findViewById<TextView>(R.id.cancelRemoveSearch).setOnClickListener {
            mSearchVM.deleteSearch(
                search_history(id = id_search, search_history = search_symbol)
            )
            dismiss()
        }

        return view
    }

}