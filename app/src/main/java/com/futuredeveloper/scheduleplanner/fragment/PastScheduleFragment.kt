package com.futuredeveloper.scheduleplanner.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.MainActivity
import com.futuredeveloper.scheduleplanner.adapter.MainRecyclerAdapter
import com.futuredeveloper.scheduleplanner.callback.SwipeGesture
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the com.example.scheduleplanner.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PastScheduleFragment.newInstance] factory method to
 * create an instance of this com.example.scheduleplanner.fragment.
 */
class PastScheduleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: MainRecyclerAdapter
    private lateinit var noScheduleText: TextView

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
        val view = inflater.inflate(R.layout.fragment_past_schedule, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        noScheduleText = view.findViewById(R.id.text2)

        val scheduleList = HomeFragment.RetrieveScheduleItems(activity as Context).execute().get()
        val scheduleList2 = ArrayList<ScheduleEntity>()
        for(item in scheduleList){
            println("${makeDate(item.scheduleDate)},${makeDate(getTodaysDate())}")
            if(makeDate(item.scheduleDate) < makeDate(getTodaysDate())){
                scheduleList2.add(item)
            }
        }
        if(scheduleList2.isNotEmpty()){
            noScheduleText.visibility = View.GONE
        }

        recyclerAdapter =
            MainRecyclerAdapter(activity as Context, scheduleList2)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager

        val swipeGesture = object : SwipeGesture(context as Activity){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delete = androidx.appcompat.app.AlertDialog.Builder(context as Activity)
                delete.setTitle("Delete Schedule")
                delete.setMessage("Do you want to delete selected schedule?")
                delete.setPositiveButton("Yes") { text, listener ->
                    recyclerAdapter.deleteIt(viewHolder.adapterPosition)
                }
                delete.setNegativeButton("No") { text, listener ->
                    recyclerAdapter.notifyDataSetChanged()
                }

                delete.create()
                delete.show()
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerHome)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this com.example.scheduleplanner.fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of com.example.scheduleplanner.fragment PastScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PastScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        month += 1
        val day = cal[Calendar.DAY_OF_MONTH]
        return makeDateString(day, month, year)
    }
    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return day.toString() + " " + getMonthFormat(month) + " " + year
    }
    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC" else "JAN"
    }

    private var date1 = StringBuilder()
    private fun makeDate(scheduleDate: String): String{
        var count = 0

        var day = ""
        var month = ""
        var year = ""

        val temp = StringBuilder()

        for (i in scheduleDate.indices){

            if(scheduleDate[i] != ' '){
                temp.append(scheduleDate[i])
            }else{
                if(count == 0){
                    if(temp.toString().length < 2){
                        day = "0$temp"
                    }else{
                        day = temp.toString()
                    }
                }else if(count == 1){
                    month = getMonthFormat1(temp.toString()).toString()
                    if(month.length < 2){
                        month = "0${month}"
                    }
                }
                temp.clear()
                count++
            }
        }
        year = temp.toString()
        date1.clear()
        date1.append(year).append(month).append(day)
        return date1.toString()
    }

    private fun getMonthFormat1(month: String): Int {
        if (month == "JAN") return 1
        if (month == "FEB") return 2
        if (month == "MAR") return 3
        if (month == "APR") return 4
        if (month == "MAY") return 5
        if (month == "JUN") return 6
        if (month == "JUL") return 7
        if (month == "AUG") return 8
        if (month == "SEP") return 9
        if (month == "OCT") return 10
        if (month == "NOV") return 11
        return if (month == "DEC")  12 else 1
    }
}