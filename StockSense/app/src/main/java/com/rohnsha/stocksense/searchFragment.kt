package com.rohnsha.stocksense

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rohnsha.stocksense.database.search_history.searchDAO
import com.rohnsha.stocksense.database.search_history.searchHistoryAdapter
import com.rohnsha.stocksense.database.search_history.search_history
import com.rohnsha.stocksense.database.search_history.search_history_model
import com.rohnsha.stocksense.docs.searchbar_docs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
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
    private var mInterstitialAd: InterstitialAd? = null

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
        val qManual= view.findViewById<ImageView>(R.id.qManual)
        mSearchHistoryModel= ViewModelProvider(this)[search_history_model::class.java]

        qManual.setOnClickListener {
            startActivity(Intent(requireContext(), searchbar_docs::class.java))
        }


        lifecycleScope.launch(Dispatchers.IO){
            val searchDBscope= launch {
                Log.e("countDBsearhc", "countingDB")
                if (mSearchHistoryModel.countDBquery()<=0){
                    withContext(Dispatchers.Main){
                        loadingSearch.visibility= View.GONE
                        initBoiler.visibility= View.VISIBLE
                        recyclerViewSearch.visibility= View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main){
                        loadingSearch.visibility= View.GONE
                        initBoiler.visibility= View.GONE
                        recyclerViewSearch.visibility= View.VISIBLE
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
                lifecycleScope.launch(Dispatchers.IO){
                    addHistoryToDB(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val adapterSearch= searchHistoryAdapter()
        recyclerViewSearch.adapter= adapterSearch
        recyclerViewSearch.layoutManager= LinearLayoutManager(requireContext())
        mSearchHistoryModel.readSearchHistory.observe(viewLifecycleOwner, Observer { history ->
            adapterSearch.setSearchHistory(history)
        })

        var adRequest = AdRequest.Builder().build()

        val adID= requireContext().getString(R.string.interstitialID)
        InterstitialAd.load(requireContext(),adID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError?.toString()?.let { Log.d("erroredAd", it) }
                mInterstitialAd = null
                InterstitialAd.load(requireContext(),adID, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError?.toString()?.let { Log.d("erroredAd", it) }
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("adLoaded", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("adLoaded", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("TAG", "Ad was clicked.")
            }
            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("TAG", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }
            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("TAG", "Ad recorded an impression.")
            }
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("TAG", "Ad showed fullscreen content.")
            }
        }
    }

    suspend fun addHistoryToDB(query: String?){
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
                val intent = Intent(requireContext(), stocksInfo::class.java)
                intent.putExtra("symbol", inputSymbol)
                startActivity(intent)
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }
        }
        return true
    }

    private fun searchIndex(stockSymbol: String): String{
        val rbNSE= view?.findViewById<RadioButton>(R.id.rbNSE)
        val rbBSE= view?.findViewById<RadioButton>(R.id.rbBSE)
        val rbManual= view?.findViewById<RadioButton>(R.id.rbManual)
        var symbol= stockSymbol

        if (rbNSE!!.isChecked){
            symbol= "$stockSymbol.NS"
        } else if (rbBSE!!.isChecked){
            symbol= "$stockSymbol.BO"
        } else if (rbManual!!.isChecked){
            symbol= stockSymbol
        }
        return symbol
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