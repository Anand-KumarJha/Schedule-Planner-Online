package com.futuredeveloper.scheduleplanner.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity


class MainRecyclerAdapter(
    val context: Context,
    private val itemList: List<ScheduleEntity>
    ) : RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder>() {

        class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            var recyclerHome: RecyclerView = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.recyclerRecyclerView)
            lateinit var layoutManager: RecyclerView.LayoutManager
            lateinit var recyclerAdapter: TaskRecyclerAdapter

            var scheduleDate: TextView = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.schedule_date)
            var scheduleDay:  TextView = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.schedule_day)
            var scheduleTitle: TextView = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.title)
            var editButton: ImageView = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.edit1)
            var deleteButton: ImageView = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.delete1)
            val liContent: RelativeLayout = view.findViewById(com.futuredeveloper.scheduleplanner.R.id.relative)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.futuredeveloper.scheduleplanner.R.layout.main_schedule_row, parent, false)
            return MainRecyclerAdapter.MainViewHolder(view)
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.scheduleDate.text = itemList[position].scheduleDate
            holder.scheduleTitle.text = itemList[position].scheduleTitle
            holder.scheduleDay.text = "Mon"
            holder.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            holder.recyclerAdapter =
                TaskRecyclerAdapter(context, itemList[position].tasks)
            holder.recyclerHome.adapter = holder.recyclerAdapter
            holder.recyclerHome.layoutManager = holder.layoutManager

            holder.editButton.setOnClickListener {
                val intent = android.content.Intent(
                    context,
                    com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity::class.java
                )
                intent.putExtra("date", holder.scheduleDate.text.toString())
                context.startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.pull_up_from_bottom,0)
                (context as Activity).finish()
            }
            holder.deleteButton.setOnClickListener{
                val logout = androidx.appcompat.app.AlertDialog.Builder(it.context)
                logout.setTitle("Delete Schedule")
                logout.setMessage("Do you want to delete selected schedule?")
                logout.setPositiveButton("Yes") { text, listener ->
                    val async = DBAsyncTask1(
                        context,
                        makeDate(holder.scheduleDate.text.toString())
                    ).execute()
                    val async2 = DBAsyncTask2(
                        context,
                        holder.scheduleDate.text.toString()
                    ).execute()
                    (it.getContext() as Activity?)?.recreate()
                    Toast.makeText(it.context,"Schedule Deleted", Toast.LENGTH_SHORT).show()
                }
                logout.setNegativeButton("No") { text, listener ->

                }
                logout.create()
                logout.show()

            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

    //For date sorting
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
        println("date = $date1, year = $year, month = $month, day = $day")
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

    class DBAsyncTask1(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase::class.java, "Schedule-Db").build()

            db.scheduleDao().deleteById(id)
            db.close()
            return true
        }
    }

    class DBAsyncTask2(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.TaskDatabase::class.java, "Task-Db").build()

            db.taskDao().clearTask(id)
            db.close()
            return true
        }
    }
}

