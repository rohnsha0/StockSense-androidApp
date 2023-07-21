package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.rohnsha.stocksense.database.search_history.search_history_model
import com.rohnsha.stocksense.indices_db.indices
import com.rohnsha.stocksense.indices_db.indicesAdapter
import com.rohnsha.stocksense.indices_db.indicesViewModel
import com.rohnsha.stocksense.ltpAPI.object_ltp.ltpAPIService
import com.rohnsha.stocksense.pred_glance_db.glance_view_model
import com.rohnsha.stocksense.pred_glance_db.pred_glance
import com.rohnsha.stocksense.prediction_api.pred_object
import com.rohnsha.stocksense.watchlist_db.watchlistAdapterFive
import com.rohnsha.stocksense.watchlist_db.watchlistsVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.properties.Delegates

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
    private lateinit var mPredictionViewModel: glance_view_model
    private lateinit var indexSymbol: String
    private lateinit var indexName: String
    private var indexPrice by Delegates.notNull<Double>()
    private lateinit var indexStatus: String
    private lateinit var glanceData: pred_glance

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

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewWl= view.findViewById<TextView>(R.id.viewWl)
        val rvWatchlists= view.findViewById<RecyclerView>(R.id.rvWatchlistHome)
        val viewAddWl= view.findViewById<TextView>(R.id.viewAddWl)
        val lottie= view.findViewById<LottieAnimationView>(R.id.lottieAnimationHome)
        val rvIndices= view.findViewById<RecyclerView>(R.id.rvIndicesHome)
        mWatchlistModel= ViewModelProvider(this)[watchlistsVM::class.java]
        mIndicesViewModel= ViewModelProvider(this)[indicesViewModel::class.java]
        mPredictionViewModel= ViewModelProvider(this)[glance_view_model::class.java]
        val addIndices= view.findViewById<Button>(R.id.addIndices)
        val glanceInit= view.findViewById<ConstraintLayout>(R.id.predictedGlanceInit)
        val glanceInitStatus= view.findViewById<TextView>(R.id.glanceInitStatus)
        val glanceContainer= view.findViewById<ConstraintLayout>(R.id.predictionGlance)

        lifecycleScope.launch(Dispatchers.IO){
            val glanceDBCount= mPredictionViewModel.getDBcount()
            if (glanceDBCount==1){
                withContext(Dispatchers.Main){
                    glanceInitStatus.text= "We're gathering informations..."
                }
                glanceData= mPredictionViewModel.queryGlance()
                withContext(Dispatchers.Main){
                    view.findViewById<TextView>(R.id.glanceName).text= glanceData.company
                    view.findViewById<TextView>(R.id.symbolGlanceTV).text= glanceData.symbol.substringBefore('.')
                }
                val dynamicURLPred= "https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/prediction/${glanceData.symbol}"
                try {
                    Log.d("glanceReq", "Sending prediction Request")
                    val responsePred= pred_object.predAPIservice.getModelData(dynamicURLPred)
                    Log.d("glanceReq", "Prediction Request sent")
                    val responseLTP= getGlanceLTP(glanceData.symbol)
                    Log.d("glanceReq", "LTP Request sent")
                    val ltp= String.format("%.2f", responseLTP!!.regularMarketPrice)
                    val predictedClose= String.format("%.2f", responsePred.predicted_close)
                    Log.d("glanceReq", "Formatting started")
                    withContext(Dispatchers.Main){
                        glanceInit.visibility= View.GONE
                        glanceContainer.visibility= View.VISIBLE
                        view.findViewById<TextView>(R.id.glanceClose).text= predictedClose
                        view.findViewById<TextView>(R.id.glanceLtp).text= ltp
                        getPredictionInfos(ltp = responseLTP!!.regularMarketPrice.toDouble(), prediction = responsePred.predicted_close.toDouble())
                    }
                    Log.d("glanceReq", "Formatting completed")
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        glanceInit.visibility= View.GONE
                        glanceContainer.visibility= View.VISIBLE
                        view.findViewById<TextView>(R.id.glanceLtp).text= glanceData.ltp.toString()
                        view.findViewById<TextView>(R.id.glanceStatus).text= glanceData.trend.uppercase()
                        view.findViewById<TextView>(R.id.glanceClose).text= String.format("%.2f", glanceData.prediction)
                        view.findViewById<TextView>(R.id.glanceRemarks).text= glanceData.remarks
                        customToast.makeText(requireContext(), "Unable to refresh Glance Dash", 2).show()
                    }
                }
            } else if (glanceDBCount==0){
                withContext(Dispatchers.Main){
                    glanceInitStatus.text= "Add items to Glance Dash from Prediction page\n1. Search for possible scripts\n2. Tap on prediction button\n3. Add script to glance dash"
                }
            } else {
                withContext(Dispatchers.Main){
                    glanceInitStatus.text= "Something went wrong. Check back later!"
                }
            }
        }

        viewAddWl.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        lifecycleScope.launch(Dispatchers.IO){
            val checkDbScope= launch {
                while (true){
                    delay(50L)
                    if (mWatchlistModel.getDBcountWL()==0){
                        withContext(Dispatchers.Main){
                            viewWl.visibility=View.GONE
                            rvWatchlists.visibility=View.GONE
                            lottie.visibility= View.VISIBLE
                            viewAddWl.visibility= View.VISIBLE
                        }
                    }
                    Log.e("checkDb", "chcking DB....")
                }
            }
            delay(2000L)
            checkDbScope.cancelAndJoin()
            Log.e("chckDB", "checking DB stopped.....")
        }

        addIndices.setOnClickListener {
            val indicesView: View= layoutInflater.inflate(R.layout.items_add, null)
            val dialgueIndices= BottomSheetDialog(requireContext())
            val tvSymbol= indicesView.findViewById<TextView>(R.id.inpSymbol_Index)
            val rbNifty50= indicesView.findViewById<RadioButton>(R.id.rbNSE50)
            val rbSensex= indicesView.findViewById<RadioButton>(R.id.rbBSE)
            val rbNSEBank= indicesView.findViewById<RadioButton>(R.id.rbNiftyBank)
            val rbGrp= indicesView.findViewById<RadioGroup>(R.id.verticalRadioIndex)
            val doneBtn= indicesView.findViewById<Button>(R.id.addBtn)
            val verifyBtn= indicesView.findViewById<TextView>(R.id.verifyIndex)
            val priceIndex= indicesView.findViewById<EditText>(R.id.priceIndex)
            val changeIndex= indicesView.findViewById<EditText>(R.id.changeIndex)
            val doneIndex= indicesView.findViewById<TextView>(R.id.doneIndex)
            dialgueIndices.setContentView(indicesView)


            rbGrp.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId==R.id.rbNSE50){
                    indexSymbol= rbNifty50.text.split(":").map { it.trim() }[1]
                    indexName= rbNifty50.text.split(":").map { it.trim() }[0]
                } else if (checkedId==R.id.rbBSE){
                    indexSymbol= rbSensex.text.split(":").map { it.trim() }[1]
                    indexName= rbSensex.text.split(":").map { it.trim() }[0]
                } else if (checkedId==R.id.rbNiftyBank){
                    indexSymbol= rbNSEBank.text.split(":").map { it.trim() }[1]
                    indexName= rbNSEBank.text.split(":").map { it.trim() }[0]
                }
                tvSymbol.text= indexSymbol
                verifyBtn.visibility= View.VISIBLE
                doneIndex.visibility= View.GONE
                changeIndex.hint= "NEUTRAL"
                priceIndex.hint= "in ₹"
            }

            dialgueIndices.show()

            verifyBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val verifyScope= launch {
                        while (true){
                            delay(50L)
                            withContext(Dispatchers.Main){
                                customToast.makeText(requireContext(), "Fetching Additional Informations", 2).show()
                            }
                            val dynamicURL= "https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/ltp/$indexSymbol"
                            try {
                                val response= ltpAPIService.getLTP(dynamicURL)
                                withContext(Dispatchers.Main){
                                    indexPrice= response.ltp
                                    priceIndex.hint= indexPrice.toString()
                                    indexStatus= response.change
                                    changeIndex.hint= indexStatus
                                    verifyBtn.visibility= View.GONE
                                    doneIndex.visibility= View.VISIBLE
                                }
                                Log.e("verifyLog", "verifying")
                            } catch (e: Exception){
                                withContext(Dispatchers.Main){
                                    customToast.makeText(requireContext(), "Something went wrong. Please try again later!", 2).show()
                                }
                            }
                        }
                    }
                    delay(1500L)
                    verifyScope.cancelAndJoin()
                }
            }

            doneIndex.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    val addDoneBtnIndexScope= launch {
                        while (true){
                            delay(50L)
                            try {
                                val indexData= indices(indexSymbol, indexName, indexPrice, indexStatus)
                                mIndicesViewModel.addIndices(indexData)
                            } catch (e: Exception){
                                val indexData= indices(indexSymbol, indexName, 0.0, "NEUTRAL")
                                mIndicesViewModel.addIndices(indexData)
                            }
                        }
                    }

                    delay(1000L)
                    addDoneBtnIndexScope.cancelAndJoin()

                    withContext(Dispatchers.Main){
                        customToast.makeText(requireContext(), "Sucessfully added to tracking", 1).show()
                    }
                }
                dialgueIndices.dismiss()
            }

            doneBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    val addIndicesScope= launch {
                        val dynamicURL= "https://45halapf2lg7zd42f33g6da7ci0kbjzo.lambda-url.ap-south-1.on.aws/ltp/$indexSymbol"
                        try {
                            val response= ltpAPIService.getLTP(dynamicURL)
                            val indexData= indices(indexSymbol, indexName, response.ltp, response.change)
                            mIndicesViewModel.addIndices(indexData)
                            Log.e("addIndex", "collecting data...")
                        } catch (e: Exception){
                            val indexData= indices(indexSymbol, indexName, 0.0, "NEUTRAL")
                            mIndicesViewModel.addIndices(indexData)
                        }
                    }
                    delay(1000L)
                    addIndicesScope.cancelAndJoin()
                    Log.e("addIndex", "cancelling...")
                }
                customToast.makeText(requireContext(), "Sucessfully added", 1).show()
                dialgueIndices.dismiss()
            }
        }

        val adapter= indicesAdapter(Application())
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
        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPreferences = requireContext().getSharedPreferences("visibilityPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            if (mSearchHistoryModel.countDBquery()<=0){
                editor.putBoolean("boilerTemp", true)
            } else{
                editor.putBoolean("boilerTemp", false)
            }
        }
    }

    suspend fun getGlanceLTP(symbol: String): com.rohnsha.stocksense.Result.Meta?{
        val url= "https://query1.finance.yahoo.com/v8/finance/chart/$symbol"
        try {
            val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
            val responseBody= response.body
            val json = responseBody?.string()
            Log.d("stockDataFetcher","JSON Response: $json")
            val gson = Gson()
            val stockDataResponse = gson.fromJson(json, StockDataResponse::class.java)
            return stockDataResponse.chart.result[0].meta
        } catch (e: Exception){
            Log.e("stockDataFetcher", "Error fetching stock data: ${e.message}")
            return null
        }
    }

    suspend fun getPredictionInfos(ltp: Double, prediction: Double){
        val remarks= view?.findViewById<TextView>(R.id.glanceRemarks)
        val trend= view?.findViewById<TextView>(R.id.glanceStatus)
        val imagePred= view?.findViewById<ImageView>(R.id.imagePred)
        val change= ltp-prediction
        if (change>0){
            trend?.text= "UPTREND"
            trend?.setTextColor(Color.GREEN)
            imagePred?.backgroundTintList= ColorStateList.valueOf(Color.GREEN)
            remarks?.text= "Stock outperforming predictions"
        } else if (change<0){
            trend?.text= "DOWNTREND"
            trend?.setTextColor(Color.RED)
            imagePred?.backgroundTintList= ColorStateList.valueOf(Color.RED)
            remarks?.text= "Bearish stock performance"
        } else {
            trend?.text= "NEUTRAL"
            trend?.setTextColor(Color.GRAY)
            imagePred?.backgroundTintList= ColorStateList.valueOf(Color.GRAY)
            remarks?.text= "Not a strong upward force, stock is performing as expected"
        }
        withContext(Dispatchers.IO){
            val glanceUpdateData= pred_glance(symbol = glanceData.symbol, company = glanceData.company, ltp =  ltp, prediction =  prediction, remarks = remarks?.text.toString(), trend = trend?.text.toString() )
            mPredictionViewModel.addGlance(glanceUpdateData)
        }
    }
}