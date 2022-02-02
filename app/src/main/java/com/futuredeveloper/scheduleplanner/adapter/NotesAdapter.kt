package com.futuredeveloper.scheduleplanner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.activity.CreateTaskActivity
import com.futuredeveloper.scheduleplanner.activity.NotesActivity
import com.futuredeveloper.scheduleplanner.classes.AlarmService
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase
import java.text.SimpleDateFormat

class NotesAdapter(val context: Context,
private val itemList: List<ScheduleEntity>
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var timeInMillis: Long = 0L
    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var count: TextView = view.findViewById(R.id.count)
        var scheduleDate: TextView = view.findViewById(R.id.date)
        var editButton: ImageView = view.findViewById(R.id.edit3)
        var deleteButton: ImageView = view.findViewById(R.id.delete3)
        var description: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notes_single_row, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.scheduleDate.text = itemList[position].scheduleDate
        holder.count.text = (1+position).toString()
        holder.description.text = itemList[position].scheduleDateNotes

        holder.editButton.setOnClickListener {
            val intent = Intent(context, NotesActivity::class.java)
            intent.putExtra("date",itemList[position].scheduleDate)
            intent.putExtra("notesDescription",itemList[position].scheduleDateNotes)
            intent.putExtra("title", itemList[position].scheduleTitle)
            context.startActivity(intent)
            (context as Activity).overridePendingTransition(R.anim.pull_up_from_top,R.anim.push_out_to_bottom)
            (context as Activity).finish()
        }
        holder.deleteButton.setOnClickListener{
            val delete = androidx.appcompat.app.AlertDialog.Builder(it.context)
            delete.setTitle("Remove Notes")
            delete.setMessage("Do you want to remove selected notes?")

            val schedule = ScheduleEntity(itemList[position].schedule_id,itemList[position].scheduleDate,itemList[position].scheduleTitle,"",itemList[position].tasks)
            delete.setPositiveButton("Yes") { text, listener ->
                val async = CreatePlanActivity.DBAsyncTask1(
                    context,
                    schedule,
                    1
                ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Notes Deleted!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                (context as Activity).recreate()
            }
            delete.setNegativeButton("No") { text, listener ->

            }
            delete.create()
            delete.show()
        }

    }

    fun delete(scheduleDate: String){
        DBAsyncTask1(
            context,
            makeDate(scheduleDate)
        ).execute()

        (context as Activity?)?.recreate()
        Toast.makeText(context,"Schedule Deleted", Toast.LENGTH_SHORT).show()
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


    private var date2 = StringBuilder()
    private fun makeDate2(scheduleDate: String): String{
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
        date2.clear()
        date2.append(day).append("-").append(month).append("-").append(year)
        return date2.toString()
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
    class DBAsyncTask1(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase::class.java, "Schedule-Db").build()

            db.scheduleDao().deleteById(id)
            db.close()
            return true
        }
    }



}