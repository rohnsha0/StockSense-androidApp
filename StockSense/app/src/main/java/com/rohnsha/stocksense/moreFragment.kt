package com.rohnsha.stocksense

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.telephony.ims.RcsUceAdapter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.rohnsha.stocksense.docs.data_docs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [moreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class moreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var auth: FirebaseAuth

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
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment moreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            moreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName= view.findViewById<TextView>(R.id.tvNameMore)
        val viewProfile= view.findViewById<View>(R.id.viewProfile)
        val viewEmail= view.findViewById<ConstraintLayout>(R.id.viewProfileEmail)
        val emailImgBtn= view.findViewById<ImageView>(R.id.emailVerifyBtn)
        val emailImgTv= view.findViewById<TextView>(R.id.emailVerifyTv)
        val changeMail= view.findViewById<TextView>(R.id.changeMail)
        val accountDelete= view.findViewById<ConstraintLayout>(R.id.accountDelete)
        val srcBtn= view.findViewById<ImageView>(R.id.srcBtn)
        val srcBtnApp= view.findViewById<ImageView>(R.id.srcBtnApp)
        val rateAppp= view.findViewById<ImageView>(R.id.rateApp)

        rateAppp.setOnClickListener {
            val reviewManager= ReviewManagerFactory.create(requireContext().applicationContext)
            reviewManager.requestReviewFlow().addOnCompleteListener {
                if (it.isSuccessful){
                    reviewManager.launchReviewFlow(requireContext() as Activity, it.result)
                }
            }
        }

        view.findViewById<ImageView>(R.id.downloadMedbuddy).setOnClickListener {
            customToast.makeText(requireContext(), "App under development!", 2).show()
        }

        srcBtn.setOnClickListener {
            val githubUrl = "https://github.com/rohnsha0/medbuddyAI"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
            startActivity(intent)
        }

        srcBtnApp.setOnClickListener {
            val githubUrl = "https://github.com/rohnsha0/stocksense"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
            startActivity(intent)
        }

        view.findViewById<ConstraintLayout>(R.id.viewHelp).setOnClickListener {
            startActivity(Intent(requireContext(), help_support::class.java))
        }

        view.findViewById<ConstraintLayout>(R.id.viewPrivacy).setOnClickListener {
            startActivity(Intent(requireContext(), data_docs::class.java))
        }

        view.findViewById<ConstraintLayout>(R.id.viewStatus).setOnClickListener {
            startActivity(Intent(requireContext(), service_status::class.java))
            //customToast.makeText(requireContext(), "Services, up and running!", 1).show()
        }

        accountDelete.setOnClickListener {
            startActivity(Intent(requireContext(), logout_account::class.java))
        }

        viewProfile.setOnClickListener {
            startActivity(Intent(requireContext(), profile::class.java))
        }

        auth= FirebaseAuth.getInstance()

        userName.text= auth.currentUser?.displayName

        if (auth.currentUser!!.isEmailVerified){
            emailImgBtn.visibility= View.GONE
            changeMail.visibility= View.VISIBLE
            emailImgTv.text= "Email id verified"
            emailImgTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.deactiveText))
            view.findViewById<ConstraintLayout>(R.id.viewProfileEmail).backgroundTintList= ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.viewDashLowOpac)
            )
        } else {
            viewEmail.setOnClickListener {
                auth.currentUser!!.sendEmailVerification()
                customToast.makeText(requireContext(), "Verification mail send to registered mail id!", 2).show()
            }
        }
    }
}