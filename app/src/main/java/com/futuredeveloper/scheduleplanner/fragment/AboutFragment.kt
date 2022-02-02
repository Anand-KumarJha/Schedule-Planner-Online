package com.futuredeveloper.scheduleplanner.fragment

import android.content.Intent
import android.content.Intent.EXTRA_EMAIL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.futuredeveloper.scheduleplanner.R


// TODO: Rename parameter arguments, choose names that match
// the com.example.scheduleplanner.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this com.example.scheduleplanner.fragment.
 */
class AboutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var supportButton: Button

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
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        supportButton = view.findViewById(R.id.contactButton)

        supportButton.setOnClickListener {
            val addresses = arrayOf("futuredeveloperx@gmail.com")
            val intent1 = Intent(Intent.ACTION_SEND)
            intent1.type = "*/*"
            intent1.putExtra(EXTRA_EMAIL, addresses)
            intent1.putExtra(Intent.EXTRA_SUBJECT, "Support Regarding Schedule Planner App")
            intent1.putExtra(Intent.EXTRA_TEXT, "")

            startActivity(Intent.createChooser(intent1,"Send mail with"))
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
         * @return A new instance of com.example.scheduleplanner.fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}