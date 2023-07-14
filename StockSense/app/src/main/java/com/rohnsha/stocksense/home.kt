package com.rohnsha.stocksense

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.rohnsha.stocksense.database.search_history.search_history_model
import com.rohnsha.stocksense.indices_db.indices
import com.rohnsha.stocksense.indices_db.indicesAdapter
import com.rohnsha.stocksense.indices_db.indicesViewModel
import com.rohnsha.stocksense.watchlist_db.watchlistAdapterFive
import com.rohnsha.stocksense.watchlist_db.watchlistsVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [home.newInstance] factory method to
 * create an instance of this fragment.
 */
class home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mSearchHistoryModel: search_history_model
    private lateinit var auth: FirebaseAuth
    private var fragmentChangeListener: FragmentChangeListener? = null
    private lateinit var mWatchlistModel: watchlistsVM
    private lateinit var mIndicesViewModel: indicesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth= FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    interface FragmentChangeListener{
        fun replaceFrag(fragment: Fragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentChangeListener = context as? FragmentChangeListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewWl= view.findViewById<TextView>(R.id.viewWl)
        val rvWatchlists= view.findViewById<RecyclerView>(R.id.rvWatchlistHome)
        val rvIndices= view.findViewById<RecyclerView>(R.id.rvIndicesHome)
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]
        mIndicesViewModel= ViewModelProvider(this)[indicesViewModel::class.java]

        val adapter= indicesAdapter()
        rvIndices.adapter= adapter
        rvIndices.layoutManager= LinearLayoutManager(requireContext())
        mIndicesViewModel.readIndices.observe(viewLifecycleOwner, Observer { stocks ->
            adapter.setIndices(stocks)
        })

        val adapterFive= watchlistAdapterFive()
        rvWatchlists.adapter= adapterFive
        rvWatchlists.layoutManager= LinearLayoutManager(requireContext())
        mWatchlistModel.readWatchlists.observe(viewLifecycleOwner, Observer { stocks ->
            adapterFive.setWatchlists(stocks)
        })

        viewWl.setOnClickListener {
            fragmentChangeListener?.replaceFrag(watchListFRAG())
        }

        checkLoginState()

        /*dltProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Profile deletion initiated", Toast.LENGTH_SHORT).show()
            auth.currentUser?.delete()
            checkLoginState()
        }

        btnSignOut.setOnClickListener {
            if (auth.currentUser==null){
                Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(requireContext(), "Signing you out", Toast.LENGTH_SHORT).show()
                auth.signOut()
                checkLoginState()
            }
        }

        if (auth.currentUser==null){
            btnLogin.visibility=View.VISIBLE
            btnSignOut.visibility =View.GONE
            dltProfile.visibility = View.GONE
            btnLogin.setOnClickListener {
                startActivity(Intent(requireActivity(), welcome_screen::class.java))
            }
        }*/
    }

    private fun checkLoginState(){
        //val textView= view?.findViewById<TextView>(R.id.homePage)
        if (auth.currentUser==null){
            val intent= Intent(requireContext(), welcome_screen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else{
        //    textView?.text= "Hello ${auth.currentUser?.displayName},\nThe Homepage will be live soon...\n\nNote: Only NIFTY50 stocks are available\nfor prediction\n(We're working on adding more!)"
        }
    }

    private fun checkUpdateSearchHistory(){
        GlobalScope.launch(Dispatchers.IO) {
            val sharedPreferences = requireContext().getSharedPreferences("visibilityPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            if (mSearchHistoryModel.countDBquery()<=0){
                editor.putBoolean("boilerTemp", true)
            } else{
                editor.putBoolean("boilerTemp", false)
            }
        }
    }
}