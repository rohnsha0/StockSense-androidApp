package com.rohnsha.stocksense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import watchlistDC



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [watchListFRAG.newInstance] factory method to
 * create an instance of this fragment.
 */
class watchListFRAG : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_watch_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment watchListFRAG.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            watchListFRAG().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView= view.findViewById<RecyclerView>(R.id.rvWatchlist)

        var wtchlist= mutableListOf(
            watchlistDC("ITC.NS", "ITC Ltd."),
            watchlistDC("INFY.NS", "Infosys Ltd."),
            watchlistDC("KOTAKBANK.NS", "Kotak Mahindra Bank Ltd."),
            watchlistDC("ASIANPAINT.NS", "Asian Paints Ltd."),
            watchlistDC("BAJAJFINSV.NS", "Bajaj Finserv Ltd."),
            watchlistDC("CIPLA.NS", "Cipla Ltd."),
            watchlistDC("HCLTECH.NS", "HCL Technologies Ltd."),
            watchlistDC("HDFC.NS", "HDFC Bank Ltd."),
            watchlistDC("APOLLOHOSP.NS", "Apollo Hospitals Enterprise Ltd."),
            watchlistDC("GRASIM.NS", "Grasim Industries Ltd.")
        )

        val adapter= watchlistAdapter(wtchlist)
        recyclerView.adapter= adapter
        recyclerView.layoutManager= LinearLayoutManager(requireContext())

        Log.d("watchlistsTF", wtchlist.isEmpty().toString())
        val buttonSearch = view.findViewById<Button>(R.id.button2search)

        buttonSearch.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }
}