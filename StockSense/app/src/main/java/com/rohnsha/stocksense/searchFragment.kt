package com.rohnsha.stocksense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.database.search_history.searchDAO
import com.rohnsha.stocksense.database.search_history.searchHistoryAdapter
import com.rohnsha.stocksense.database.search_history.search_history
import com.rohnsha.stocksense.database.search_history.search_history_model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [searchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mSearchHistoryModel: search_history_model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment searchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            searchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView= view.findViewById<SearchView>(R.id.searchClickFrag)
        mSearchHistoryModel= ViewModelProvider(this)[search_history_model::class.java]

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                GlobalScope.launch(Dispatchers.IO){
                    addHistoryToDB(query)
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "successfully added", Toast.LENGTH_SHORT).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val recyclerViewSearch= view.findViewById<RecyclerView>(R.id.searchHistoryRV)
        val adapterSearch= searchHistoryAdapter()
        recyclerViewSearch.adapter= adapterSearch
        recyclerViewSearch.layoutManager= LinearLayoutManager(requireContext())
        mSearchHistoryModel.readSearchHistory.observe(viewLifecycleOwner, Observer { history ->
            adapterSearch.setSearchHistory(history)
        })
    }

    suspend fun addHistoryToDB(query: String?){
        val queryDB= search_history(0, query)
        mSearchHistoryModel.addHistory(queryDB)
    }

    private fun performSearch(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Field can't be null", Toast.LENGTH_SHORT).show()
            return false
        } else {
            if (query.matches(Regex("[-+]?\\d+(\\.\\d+)?"))) {
                Toast.makeText(requireContext(), "Enter a valid input", Toast.LENGTH_SHORT).show()
            } else {
                val inputSymbol = "$query.NS"
                val intent = Intent(requireContext(), stocksInfo::class.java)
                intent.putExtra("symbol", inputSymbol)
                startActivity(intent)
            }
        }
        return true
    }
}