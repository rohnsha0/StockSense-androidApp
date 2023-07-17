package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rohnsha.stocksense.watchlist_db.watchlistAdapterFive
import com.rohnsha.stocksense.watchlist_db.watchlistsAdapter
import com.rohnsha.stocksense.watchlist_db.watchlistsVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
 * Use the [watchListFRAG.newInstance] factory method to
 * create an instance of this fragment.
 */
class watchListFRAG : Fragment() {

    private val WATCHLIST_PREFS = "watchlist_prefs"
    private val WATCHLIST_KEY = "watchlist_data"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mWatchlistModel: watchlistsVM
    private lateinit var sortView: ConstraintLayout
    private lateinit var assetICO: ImageView
    private lateinit var sortingActive: TextView
    private lateinit var clearTV: TextView
    private lateinit var assetTV: TextView

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

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortView= view.findViewById(R.id.viewSorting)
        val recyclerView= view.findViewById<RecyclerView>(R.id.rvWatchlist)
        var lastState= R.id.alphaRadio
        var lastStateV= R.id.rbAsc
        sortingActive= view.findViewById(R.id.sortingActive)
        clearTV= view.findViewById(R.id.clearTV)
        assetICO= view.findViewById(R.id.assetIco)
        val dp2px= context?.resources?.displayMetrics?.density
        assetTV= view.findViewById(R.id.assetsTV)
        var configCount= 0
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]

        val adapter= watchlistsAdapter()
        recyclerView.adapter= adapter
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        mWatchlistModel.readWatchlists.observe(viewLifecycleOwner, Observer { stocks ->
            adapter.setWatchlists(stocks)
        })

        clearTV.setOnClickListener {
            mWatchlistModel.readWatchlists.observe(viewLifecycleOwner, Observer { stocks ->
                adapter.setWatchlists(stocks)
            })
            resetUI()
            lastState= R.id.alphaRadio
            lastStateV= R.id.rbAsc
            customToast.makeText(requireContext(), "Reset sorting configurations", 1).show()
        }

        sortView.setOnClickListener {
            val viewSort: View= layoutInflater.inflate(R.layout.bottom_sheet_sortings, null)
            val dialogInp= BottomSheetDialog(requireContext())
            dialogInp.setContentView(viewSort)

            val doneTV= viewSort.findViewById<TextView>(R.id.doneTV)
            val radioGrp= viewSort.findViewById<RadioGroup>(R.id.radio2grp)
            val verticalRadio= viewSort.findViewById<RadioGroup>(R.id.verticalRadio)
            val rbAlphabet= viewSort.findViewById<RadioButton>(R.id.alphaRadio)
            val rbLTP= viewSort.findViewById<RadioButton>(R.id.rbLTP)
            val rbStatus= viewSort.findViewById<RadioButton>(R.id.rbStatus)
            val rbAsc= viewSort.findViewById<RadioButton>(R.id.rbAsc)
            val rbDesc= viewSort.findViewById<RadioButton>(R.id.rbDesc)

            viewSort.findViewById<RadioButton>(lastState).isChecked= true
            viewSort.findViewById<RadioButton>(lastStateV).isChecked= true

            radioGrp.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != R.id.alphaRadio){
                    rbAlphabet.isChecked= false
                }
            }

            doneTV.setOnClickListener {
                if (rbAlphabet.isChecked && rbAsc.isChecked){
                    if (configCount>=1){
                        mWatchlistModel.readWatchlists.observe(viewLifecycleOwner, Observer { stocks ->
                            adapter.setWatchlists(stocks)
                        })
                        resetUI()
                        lastState= R.id.alphaRadio
                    }
                } else if (rbAlphabet.isChecked && rbDesc.isChecked){
                    mWatchlistModel.readWatchlistsDesc.observe(viewLifecycleOwner, Observer { stocks ->
                        adapter.setWatchlists(stocks)
                    })
                    sortedUI()
                    lastState= R.id.alphaRadio
                    lastStateV= R.id.rbDesc
                    configCount++
                } else if (rbLTP.isChecked && rbAsc.isChecked){
                    mWatchlistModel.sortLTPAsc.observe(viewLifecycleOwner, Observer { stocks ->
                        adapter.setWatchlists(stocks)
                    })
                    lastState= R.id.rbLTP
                    lastStateV= R.id.rbAsc
                    sortedUI()
                    configCount++
                } else if (rbStatus.isChecked && rbAsc.isChecked){
                    mWatchlistModel.sortStatusAsc.observe(viewLifecycleOwner, Observer { stocks ->
                        adapter.setWatchlists(stocks)
                    })
                    sortedUI()
                    lastState= R.id.rbStatus
                    lastStateV= R.id.rbAsc
                    configCount++
                } else if (rbStatus.isChecked && rbDesc.isChecked){
                    mWatchlistModel.sortStatusDesc.observe(viewLifecycleOwner, Observer { stocks ->
                        adapter.setWatchlists(stocks)
                    })
                    sortedUI()
                    lastState= R.id.rbStatus
                    lastStateV= R.id.rbDesc
                    configCount++
                }
                else if (rbLTP.isChecked && rbDesc.isChecked){
                    mWatchlistModel.sortLTPDesc.observe(viewLifecycleOwner, Observer { stocks ->
                        adapter.setWatchlists(stocks)
                    })
                    sortedUI()
                    configCount++
                    lastState= R.id.rbLTP
                    lastStateV= R.id.rbDesc
                }
                dialogInp.dismiss()
                customToast.makeText(requireContext(), "Successfully applied configurations", 1).show()
            }

            dialogInp.show()
        }

        val welcomeContainer= view.findViewById<ConstraintLayout>(R.id.welcomeContainer)
        val mainContainer= view.findViewById<ConstraintLayout>(R.id.mainContentWatchlists)

        lifecycleScope.launch(Dispatchers.IO) {
            val wlDBcount= launch {
                Log.e("checkingDBWL", "checking db for wl.....")
                delay(50)
                if (mWatchlistModel.getDBcountWL()==0){
                    withContext(Dispatchers.Main){
                        welcomeContainer.visibility= View.VISIBLE
                        mainContainer.visibility=View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main){
                        welcomeContainer.visibility= View.GONE
                        mainContainer.visibility=View.VISIBLE
                    }
                }
            }
            delay(2000L)
            wlDBcount.cancelAndJoin()
            Log.e("checkingDBWL", "stooping db for wl.....")
        }

        val buttonSearch = view.findViewById<Button>(R.id.button2search)

        buttonSearch.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }

    private fun sortedUI(){
        val dp2px= context?.resources?.displayMetrics?.density

        sortView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            marginStart= (88* dp2px!!).toInt()
        }
        assetICO.updateLayoutParams<ConstraintLayout.LayoutParams> {
            marginStart= (33* dp2px!!).toInt()
        }

        sortView.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.dash_bg))
        assetTV.setTextColor(resources.getColor(R.color.white))
        assetICO.imageTintList= ColorStateList.valueOf(resources.getColor(R.color.white))
        sortingActive.visibility= View.VISIBLE
        clearTV.visibility=View.VISIBLE
    }

    private fun resetUI(){
        val dp2px= context?.resources?.displayMetrics?.density
        sortView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            marginStart= (24* dp2px!!).toInt()
        }

        assetICO.updateLayoutParams<ConstraintLayout.LayoutParams> {
            marginStart= (14* dp2px!!).toInt()
        }

        sortView.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.viewsDash))
        view?.findViewById<TextView>(R.id.assetsTV)?.setTextColor(resources.getColor(R.color.dash_bg))
        assetICO.imageTintList= ColorStateList.valueOf(resources.getColor(R.color.dash_bg))
        sortingActive.visibility= View.GONE
        clearTV.visibility= View.GONE
    }

}