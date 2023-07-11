package com.rohnsha.stocksense

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rohnsha.stocksense.watchlist_db.watchlistsAdapter
import com.rohnsha.stocksense.watchlist_db.watchlistsVM
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

    private val WATCHLIST_PREFS = "watchlist_prefs"
    private val WATCHLIST_KEY = "watchlist_data"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mWatchlistModel: watchlistsVM

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
        val sortView= view.findViewById<ConstraintLayout>(R.id.viewSorting)
        val recyclerView= view.findViewById<RecyclerView>(R.id.rvWatchlist)
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]

        val adapter= watchlistsAdapter()
        recyclerView.adapter= adapter
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        mWatchlistModel.readWatchlists.observe(viewLifecycleOwner, Observer { stocks ->
            adapter.setWatchlists(stocks)
        })

        sortView.setOnClickListener {
            val viewSort: View= layoutInflater.inflate(R.layout.bottom_sheet_sortings, null)
            val dialogInp= BottomSheetDialog(requireContext())
            dialogInp.setContentView(viewSort)
            val bottomImg= viewSort.findViewById<TextView>(R.id.doneTV)
            bottomImg.setOnClickListener {
                dialogInp.dismiss()
                customToast.makeText(requireContext(), "Successfully applied configurations", 1).show()
            }
            dialogInp.show()
        }

        val buttonSearch = view.findViewById<Button>(R.id.button2search)

        buttonSearch.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }

}