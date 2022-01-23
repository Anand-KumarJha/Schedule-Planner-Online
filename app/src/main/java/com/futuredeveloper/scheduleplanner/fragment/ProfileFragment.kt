package com.futuredeveloper.scheduleplanner.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.Login
import com.futuredeveloper.scheduleplanner.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import java.util.concurrent.Executor

// TODO: Rename parameter arguments, choose names that match
// the com.example.scheduleplanner.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this com.example.scheduleplanner.fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var logout: Button
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var userId: String

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // Inflate the layout for this com.example.scheduleplanner.fragment
        logout = view.findViewById(R.id.logout)
        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid.toString()

        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val documentReference = fStore.collection("users").document(userId)
        documentReference.get().addOnSuccessListener {
            if(it.exists()){
                name.text = it.getString("userName")
                email.text = it.getString("userEmail")
            }
        }
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, Login::class.java)
            startActivity(intent)
            Toast.makeText(context, "Logged Out", Toast.LENGTH_LONG).show()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this com.example.scheduleplanner.fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of com.example.scheduleplanner.fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}