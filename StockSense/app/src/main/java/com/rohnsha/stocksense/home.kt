package com.rohnsha.stocksense

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

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

    private lateinit var auth: FirebaseAuth

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnSignOut= view.findViewById<Button>(R.id.btnSignOut)
        val textView= view.findViewById<TextView>(R.id.homePagw)
        val dltProfile= view.findViewById<Button>(R.id.dltUser)
        checkLoginState()

        dltProfile.setOnClickListener {
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
                startActivity(Intent(requireActivity(), login::class.java))
            }
        }
    }

    private fun checkLoginState(){
        val textView= view?.findViewById<TextView>(R.id.homePagw)
        if (auth.currentUser==null){
            startActivity(Intent(requireContext(), login::class.java))
        } else{
            textView?.text= "Hello, ${auth.currentUser?.displayName}"
        }
    }
}