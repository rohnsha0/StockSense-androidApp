package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rohnsha.stocksense.database.search_history.searchHistoryAdapter
import com.rohnsha.stocksense.database.search_history.search_history
import com.rohnsha.stocksense.database.search_history.search_history_model
import com.rohnsha.stocksense.database.search_history.search_suggestions.stocksModel
import com.rohnsha.stocksense.database.search_history.search_suggestions.stocksSearchAdapter
import com.rohnsha.stocksense.docs.searchbar_docs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
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
    private lateinit var mStocksViewModel: stocksModel
    private lateinit var rbManual: RadioButton
    private lateinit var rbNSE: RadioButton
    private lateinit var rbBSE: RadioButton
    private lateinit var recyclerViewSearch: RecyclerView
    private lateinit var filterNotice: TextView

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
        val recyclerViewSearch= view.findViewById<RecyclerView>(R.id.searchHistoryRV)
        val initBoiler= view.findViewById<ConstraintLayout>(R.id.searchInit)
        val loadingSearch= view.findViewById<ConstraintLayout>(R.id.loadingSearch)
        val filterNotice= view.findViewById<TextView>(R.id.filterSearchNotice)
        val rbNSE= view.findViewById<RadioButton>(R.id.rbNSE)
        val rbBSE= view.findViewById<RadioButton>(R.id.rbBSE)
        val rbManual= view.findViewById<RadioButton>(R.id.rbManual)
        val qManual= view.findViewById<ImageView>(R.id.qManual)
        mSearchHistoryModel= ViewModelProvider(this)[search_history_model::class.java]
        mStocksViewModel= ViewModelProvider(this)[stocksModel::class.java]

        searchView.isIconified= false
        searchView.background= null

        val adapterSearch= searchHistoryAdapter(Application())
        recyclerViewSearch.adapter= adapterSearch
        recyclerViewSearch.layoutManager= LinearLayoutManager(requireContext())
        mSearchHistoryModel.readSearchHistory.observe(viewLifecycleOwner, Observer { history ->
            adapterSearch.setSearchHistory(history)
        })

        qManual.setOnClickListener {
            startActivity(Intent(requireContext(), searchbar_docs::class.java))
        }

        lifecycleScope.launch(Dispatchers.IO){
            val searchDBscope= launch {
                Log.e("countDBsearhc", "countingDB")
                if (mSearchHistoryModel.countDBquery()<=0){
                    withContext(Dispatchers.Main){
                        initBoiler.visibility= View.VISIBLE
                        recyclerViewSearch.visibility= View.GONE
                    }
                }
            }

            delay(2000L)
            searchDBscope.cancelAndJoin()
            Log.e("countDBsearhc", "stoopingCounting")
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return false
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()){
                    val adapterStock= stocksSearchAdapter(Application())
                    recyclerViewSearch.adapter= adapterStock
                    recyclerViewSearch.layoutManager= LinearLayoutManager(requireContext())
                    val query= "%$newText%"
                    //recyclerViewSearch.setPadding(0, 0, 0, dp2px(15))
                    //filterNotice.visibility= View.VISIBLE
                    lifecycleScope.launch(Dispatchers.IO){
                        if (rbNSE.isChecked){
                            val stockData= mStocksViewModel.searchStocksDBNSE(query)
                            withContext(Dispatchers.Main){
                                stockData.observe(viewLifecycleOwner) { queryInp ->
                                    queryInp.let {
                                        adapterStock.setStocksList(it)
                                    }
                                }
                            }
                        } else if (rbBSE.isChecked){
                            val stockDataBSE= mStocksViewModel.searchStocksDBBSE(query)
                            withContext(Dispatchers.Main){
                                stockDataBSE.observe(viewLifecycleOwner){ queryBSE ->
                                    queryBSE.let {
                                        adapterStock.setStocksList(it)
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main){
                                showHistory()
                            }
                        }
                    }
                } else {
                    showHistory()
                }
                return true
            }
        })
    }

    fun addHistoryToDB(query: String?){
        val queryDB= search_history(0, query)
        mSearchHistoryModel.addHistory(queryDB)
    }

    private fun performSearch(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            customToast.makeText(requireContext(), "Field can't be null", 2).show()
            return false
        } else {
            if (query.matches(Regex("[0-9]+"))) {
                customToast.makeText(requireContext(), "Symbol cannot be numbers", 2).show()
            } else if (query.matches(Regex("\\s"))){
                customToast.makeText(requireContext(), "Symbols cannot have spaces", 2).show()
            } else {
                val inputSymbol = searchIndex(query)
                lifecycleScope.launch(Dispatchers.IO){
                    addHistoryToDB(inputSymbol)
                }
                val intent = Intent(requireContext(), stocksInfo::class.java)
                intent.putExtra("symbol", inputSymbol)
                startActivity(intent)
            }
        }
        return true
    }

    fun dp2px(px: Int): Int {
        val density = resources.displayMetrics.density
        return (px * density).toInt()
    }

    private fun searchIndex(stockSymbol: String): String{
        var symbol= stockSymbol
        val rbNSE= view?.findViewById<RadioButton>(R.id.rbNSE)
        val rbBSE= view?.findViewById<RadioButton>(R.id.rbBSE)
        val rbManual= view?.findViewById<RadioButton>(R.id.rbManual)

        if (rbNSE!!.isChecked){
            symbol= "$stockSymbol.NS"
        } else if (rbBSE!!.isChecked){
            symbol= "$stockSymbol.BO"
        } else if (rbManual!!.isChecked){
            symbol= stockSymbol
        }
        return symbol
    }

    private fun showHistory(){
        val recyclerViewSearch= view?.findViewById<RecyclerView>(R.id.searchHistoryRV)
        val filterNotice= view?.findViewById<TextView>(R.id.filterSearchNotice)
        //recyclerViewSearch?.setPadding(0, 0, 0, dp2px(0))
        //filterNotice?.visibility= View.GONE
        val adapterSearch= searchHistoryAdapter(Application())
        recyclerViewSearch?.adapter= adapterSearch
        recyclerViewSearch?.layoutManager= LinearLayoutManager(requireContext())
        mSearchHistoryModel.readSearchHistory.observe(viewLifecycleOwner, Observer { history ->
            adapterSearch.setSearchHistory(history)
        })
    }

    private fun searchDatabaseForStocks(query: String){
        val search_query= "%$query%"
    }

    override fun onPause() {
        Log.d("mm", "onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("mm", "onDestroy")
        super.onDestroy()
    }
}