package com.futuredeveloper.scheduleplanner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.activity.CreateTaskActivity
import com.futuredeveloper.scheduleplanner.adapter.CreatePlanAdapter
import com.futuredeveloper.scheduleplanner.adapter.MainRecyclerAdapter
import com.futuredeveloper.scheduleplanner.adapter.RepeatingTaskAdapter
import com.futuredeveloper.scheduleplanner.callback.SwipeGesture
import com.futuredeveloper.scheduleplanner.classes.AlarmService
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase
import com.futuredeveloper.scheduleplanner.database.TaskEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var recyclerHome2: RecyclerView
    private lateinit var layoutManager2: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter2: CreatePlanAdapter
    private lateinit var repeatRecycler: RecyclerView
    private lateinit var repeatLayoutManager: RecyclerView.LayoutManager
    private lateinit var repeatRecyclerAdapter: RepeatingTaskAdapter

    private lateinit var createIcon: FloatingActionButton
    private lateinit var repeatedTask: FloatingActionButton
    private lateinit var tasksDone: TextView
    private lateinit var tasksDonePercentage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var editTodaySchedule: RelativeLayout
    private lateinit var deleteTodaySchedule: ImageView
    private lateinit var verticalRow: View
    private lateinit var noSchedule: RelativeLayout
    private lateinit var nestedScrollView:NestedScrollView
    private lateinit var repeatText: TextView
    private lateinit var scheduleByDateText: TextView
    private lateinit var repeatTaskBox: FrameLayout
    private var timeInMillis: Long = 0L

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
        recyclerHome2 = view.findViewById(R.id.recyclerHome2)
        layoutManager2 = LinearLayoutManager(activity)
        repeatRecycler = view.findViewById(R.id.repeat_task_recycler)
        repeatLayoutManager = LinearLayoutManager(activity, HORIZONTAL, false)

        tasksDone = view.findViewById(R.id.tasksDone)
        tasksDonePercentage = view.findViewById(R.id.tasksDonePercentage)
        progressBar = view.findViewById(R.id.progress_bar)
        editTodaySchedule = view.findViewById(R.id.editSchedule)
        deleteTodaySchedule = view.findViewById(R.id.delete)
        verticalRow = view.findViewById(R.id.vertical_row)
        noSchedule = view.findViewById(R.id.noSchedule)
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        repeatedTask = view.findViewById(R.id.repeat_icon)
        repeatText = view.findViewById(R.id.text3)
        scheduleByDateText = view.findViewById(R.id.text4)
        repeatTaskBox = view.findViewById(R.id.repeat_task_box)

        nestedScrollView.isFocusableInTouchMode = true
        nestedScrollView.fullScroll(View.FOCUS_UP)
        nestedScrollView.smoothScrollTo(0,0)

        val repeatTasksList = GetAllRepeatTasks(activity as Context).execute().get()
        if(repeatTasksList.isEmpty()){
            repeatText.visibility = View.GONE
            repeatTaskBox.visibility = View.GONE
        }
        repeatRecyclerAdapter = RepeatingTaskAdapter(activity as Context,repeatTasksList)
        repeatRecycler.setHasFixedSize(true);
        repeatRecycler.adapter = repeatRecyclerAdapter
        repeatRecycler.layoutManager = repeatLayoutManager

        val scheduleList = RetrieveScheduleItems(activity as Context).execute().get()
        val scheduleList2 = ArrayList<ScheduleEntity>()
        for(item in scheduleList){
            if(item.scheduleDate != getTodaysDate() && makeDate(item.scheduleDate) > makeDate(getTodaysDate())){
                scheduleList2.add(item)
            }
        }
        if(repeatTasksList.isNotEmpty()){
            noSchedule.visibility = View.GONE
            if(scheduleList2.isEmpty()){
                scheduleByDateText.visibility = View.GONE
            }
        }
        if(scheduleList2.size > 0){
            noSchedule.visibility = View.GONE
        }
        recyclerAdapter =
            MainRecyclerAdapter(activity as Context, scheduleList2)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager
        ViewCompat.setNestedScrollingEnabled(recyclerHome, false)
        try {
            val schedule = CreatePlanActivity.DBAsyncTask2(context as Activity, makeDate(getTodaysDate())).execute().get()
            recyclerAdapter2 =
                CreatePlanAdapter(activity as Context, schedule.tasks)
            recyclerHome2.adapter = recyclerAdapter2
            recyclerHome2.layoutManager = layoutManager2

            editTodaySchedule.setOnClickListener {
                val intent = Intent(context, CreatePlanActivity::class.java)
                intent.putExtra("date",schedule.scheduleDate)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.pull_up_from_bottom,0)
                activity?.finish()
            }
            if(schedule.tasks.isNotEmpty()) {
                var done = 0
                for(item in schedule.tasks){
                    if(item.taskDone)done++
                }
                tasksDone.text = ("" + done + "/" + schedule.tasks.size + " Tasks Done")

                val percentage = (done.toFloat() / (schedule.tasks.size).toFloat()) * 100
                tasksDonePercentage.text = percentage.toInt().toString() + "%"
                progressBar.progress = percentage.toInt()
            }else{
                tasksDone.text = ("No Tasks Scheduled")

                val percentage = 100
                tasksDonePercentage.text = percentage.toString() + "%"
                progressBar.progress = percentage
            }
        }catch (e: Exception){
            editTodaySchedule.visibility = View.GONE
            deleteTodaySchedule.visibility = View.GONE
            verticalRow.visibility = View.GONE

            tasksDone.text = ("No Tasks Scheduled")

            val percentage = 100
            tasksDonePercentage.text = percentage.toString() + "%"
            progressBar.progress = percentage
        }
        ViewCompat.setNestedScrollingEnabled(recyclerHome2, false)

        deleteTodaySchedule.setOnClickListener {
            val delete = androidx.appcompat.app.AlertDialog.Builder(it.context)
            delete.setTitle("Delete Schedule")
            delete.setMessage("Do you want to delete selected schedule?")
            delete.setPositiveButton("Yes") { text, listener ->
                val schedule = DBAsyncTask1(
                    context as Activity,
                    makeDate(getTodaysDate())
                ).execute().get()

                for(i in schedule.tasks){
                    var count1 = 0
                    var start = 0

                    val sb = java.lang.StringBuilder()
                    for(j in (i.task_id)){
                        if(j == ','){
                            count1++
                        }
                        if(count1 >= 2){
                            break
                        }
                        if(count1 == 1 && start > 0){
                            sb.append(j)
                        }
                        if(count1 == 1){
                            start++
                        }
                    }

                    timeInMillis = (sb.toString()).toLong()

                    val sb1 = StringBuilder()
                    var count = 0
                    for(j in (i.task_id)){
                        if(count >= 2){
                            sb1.append(j)
                        }
                        if(j == ',')count++
                    }
                    val alarmNo = Integer.parseInt(sb1.toString())

                    println("Canceled alarm -------------------- $alarmNo")
                    val alarmService= AlarmService(context as Activity,alarmNo,"")
                    cancelAlarm{alarmService.cancelAlarm(timeInMillis)}
                }

                MainRecyclerAdapter.DBAsyncTask1(
                    context as Activity,
                    makeDate(getTodaysDate())
                ).execute()
                MainRecyclerAdapter.DBAsyncTask2(
                    context as Activity,
                    getTodaysDate()
                ).execute()
                (context as Activity?)?.recreate()
                Toast.makeText(context,"Schedule Deleted", Toast.LENGTH_SHORT).show()
            }
            delete.setNegativeButton("No") { text, listener ->

            }
            delete.create()
            delete.show()
        }
        repeatedTask.setOnClickListener {
            val intent = Intent(context, CreateTaskActivity::class.java)
            intent.putExtra("repeat",true)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.pull_up_from_bottom,0)
            activity?.finish()
        }
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

    private fun cancelAlarm(callback: (Long) -> Unit){
        callback(timeInMillis)
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
    //
    class RetrieveScheduleItems(val context: Context) : AsyncTask<Void, Void, List<ScheduleEntity>>() {
        override fun doInBackground(vararg params: Void?): List<ScheduleEntity> {
            val db = Room.databaseBuilder(context, ScheduleRoomDatabase::class.java, "Schedule-Db").build()
            val ret = db.scheduleDao().getAllSchedule()
            db.close()
            return ret
        }

    }
    class DBAsyncTask1(val context: Context, val id: String):
        android.os.AsyncTask<Void, Void, ScheduleEntity>() {

        override fun doInBackground(vararg params: Void?): ScheduleEntity {
            val db = Room.databaseBuilder(context, ScheduleRoomDatabase::class.java, "Schedule-Db").build()

            val ret = db.scheduleDao().getScheduleById(id)
            db.close()
            return ret
        }
    }
    class GetAllRepeatTasks(val context: Context) :
        android.os.AsyncTask<Void, Void, List<TaskEntity>>() {

        override fun doInBackground(vararg params: Void?): List<TaskEntity> {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.TaskDatabase::class.java, "Task-Db").build()

            val taskEntity: List<TaskEntity> = db.taskDao().getAllRepeatTasks()
            db.close()
            return taskEntity
        }
    }

}