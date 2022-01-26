package com.futuredeveloper.scheduleplanner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.adapter.MainRecyclerAdapter
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.futuredeveloper.scheduleplanner.callback.SwipeGesture
import java.lang.Exception
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the com.example.scheduleplanner.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this com.example.scheduleplanner.fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: MainRecyclerAdapter
    private lateinit var createIcon: FloatingActionButton
    private lateinit var tasksDone: TextView
    private lateinit var tasksDonePercentage: TextView
    private lateinit var progressBar: ProgressBar

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
        // Inflate the layout for this com.example.scheduleplanner.fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        createIcon = view.findViewById(R.id.create_icon)
        tasksDone = view.findViewById(R.id.tasksDone)
        tasksDonePercentage = view.findViewById(R.id.tasksDonePercentage)
        progressBar = view.findViewById(R.id.progress_bar)

        val scheduleList = RetrieveScheduleItems(activity as Context).execute().get()

        try {
            val schedule = CreatePlanActivity.DBAsyncTask2(context as Activity, makeDate(getTodaysDate())).execute().get()
            if(schedule.tasks.size != 0) {
                val done = schedule.tasks.size - 1

                tasksDone.setText(("" + done + "/" + schedule.tasks.size + " Tasks Done"))

                val percentage = (done.toFloat() / (schedule.tasks.size).toFloat()) * 100
                tasksDonePercentage.setText(percentage.toInt().toString() + "%")
                progressBar.progress = percentage.toInt()
            }else{
                tasksDone.setText(("All Tasks Done"))

                val percentage = 100
                tasksDonePercentage.setText(percentage.toString() + "%")
                progressBar.progress = percentage
            }
        }catch (e: Exception){
            tasksDone.setText(("All Tasks Done"))

            val percentage = 100
            tasksDonePercentage.setText(percentage.toString() + "%")
            progressBar.progress = percentage
        }


        recyclerAdapter =
            MainRecyclerAdapter(activity as Context, scheduleList)

        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager


        createIcon.setOnClickListener {
            val intent = Intent(context, CreatePlanActivity::class.java)
            intent.putExtra("date","0")
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.pull_up_from_bottom,0)
            activity?.finish()
        }

        val swipeGesture = object :SwipeGesture(context as Activity){
            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val delete = androidx.appcompat.app.AlertDialog.Builder(context as Activity)
                delete.setTitle("Delete Schedule")
                delete.setMessage("Do you want to delete selected schedule?")
                delete.setPositiveButton("Yes") { text, listener ->
                    recyclerAdapter.deleteIt(viewHolder.adapterPosition)
                }
                delete.setNegativeButton("No") { text, listener ->

                }
                recyclerAdapter.notifyDataSetChanged()
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
         * @return A new instance of com.example.scheduleplanner.fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        month += 1
        val day = cal[Calendar.DAY_OF_MONTH]
        return makeDateString(day, month, year)
    }
    fun makeDateString(day: Int, month: Int, year: Int): String {
        return day.toString() + " " + getMonthFormat(month) + " " + year
    }
    fun getMonthFormat(month: Int): String {
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

    var date1 = StringBuilder()
    fun makeDate(scheduleDate: String): String{
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
                        day = "0${temp.toString()}"
                    }else{
                        day = temp.toString()
                    }
                }else if(count == 1){
                    month = getMonthFormat1(temp.toString()).toString()
                    if(month.toString().length < 2){
                        month = "0${month.toString()}"
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

    fun getMonthFormat1(month: String): Int {
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
    //
    class RetrieveScheduleItems(val context: Context) : AsyncTask<Void, Void, List<ScheduleEntity>>() {
        override fun doInBackground(vararg params: Void?): List<ScheduleEntity>? {
            val db = Room.databaseBuilder(context, ScheduleRoomDatabase::class.java, "Schedule-Db").build()
            val ret = db.scheduleDao().getAllSchedule()
            db.close()
            return ret
        }

    }

}