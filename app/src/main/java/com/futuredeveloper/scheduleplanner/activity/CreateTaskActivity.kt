package com.futuredeveloper.scheduleplanner.activity

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.classes.AlarmService
import com.futuredeveloper.scheduleplanner.database.TaskDatabase
import com.futuredeveloper.scheduleplanner.database.TaskEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CreateTaskActivity : AppCompatActivity() {
    private var timeButton: Button? = null
    private var hour = 0
    private var minute:Int = 0
    private lateinit var time: String
    lateinit var title: String
    private lateinit var description: String
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var notesDescription: String
    private var timetype = "AM"
    private lateinit var saveTask: FloatingActionButton
    private var date: String? = ""
    private var scheduleTitle = ""
    private var timeInMillis: Long = 0
    private lateinit var alarmService: AlarmService
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_task)
        sharedPreference = getSharedPreferences("schedule_planner_preference", MODE_PRIVATE)
        val alarmNo = sharedPreference.getInt("alarmNo", 0)
        if(alarmNo == Int.MAX_VALUE){sharedPreference.edit().putInt("alarmNo", 0).apply()}


        timeButton = findViewById(R.id.timeButton)
        saveTask = findViewById(R.id.save_icon)
        titleEditText = findViewById(R.id.title)
        descriptionEditText = findViewById(R.id.description)

        if(intent.getStringExtra("taskId") != null){
            timeButton?.text = intent.getStringExtra("taskTime").toString()
            timeButton?.isEnabled = false
            titleEditText.setText(intent.getStringExtra("taskTitle").toString())
            descriptionEditText.setText(intent.getStringExtra("taskDescription").toString())
        }

        date = intent.getStringExtra("date")
        notesDescription = intent.getStringExtra("notesDescription").toString()
        scheduleTitle = intent.getStringExtra("title").toString()

        //Default Time in millis
        val calendar = Calendar.getInstance()
        val date1 = makeDate2(date.toString()) + " 00:00:00"
        val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        try {
            val date: Date = sdf.parse(date1)
            calendar.time = date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        println(calendar.timeInMillis)
        timeInMillis = calendar.timeInMillis
        //

        saveTask.setOnClickListener {
            val calendar = Calendar.getInstance()
            hour = Integer.parseInt(timeButton?.text?.subSequence(0, 2).toString())
            minute = Integer.parseInt(timeButton?.text?.subSequence(3, 5).toString())
            timetype = (timeButton?.text?.subSequence(6, 8).toString())

            if (timetype == "PM") {
                if(hour != 12){
                    hour += 12
                }
            } else if (timetype == "AM") {
                if (hour == 12) {
                    hour = 0
                }
            }

            val date1 = makeDate2(date.toString()) + " $hour:$minute:00"
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            try {
                val date: Date = sdf.parse(date1)
                calendar.time = date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            timeInMillis = calendar.timeInMillis
            time = android.text.format.DateFormat.format("hh:mm aa", calendar).toString()
            title = titleEditText.text.toString()
            description = descriptionEditText.text.toString()

            if (intent.getStringExtra("taskId") == null) {
                sharedPreference.edit().putInt("alarmNo", alarmNo + 1).apply()

                if(title == ""){
                    alarmService = AlarmService(this, alarmNo, description)
                }else if(description == ""){
                    alarmService = AlarmService(this, alarmNo, title)
                }else if(title == "" && description == ""){
                    alarmService = AlarmService(this, alarmNo, "$title $description")
                }
                else{
                    alarmService = AlarmService(this, alarmNo, "$title - $description")
                }

                setAlarm { alarmService.setExactAlarm(it) }
                println("Created alarm ----------------$alarmNo")

                val taskId = "$date,$timeInMillis,$alarmNo"

                val taskEntity = TaskEntity(
                    taskId,
                    time,
                    title,
                    description,
                    false
                )

                val async = DBAsyncTask1(
                    this,
                    taskEntity,
                    2
                ).execute()

                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this,
                        "Task Added Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }
            }else{
                val taskEntity = TaskEntity(
                    intent.getStringExtra("taskId").toString(),
                    time,
                    title,
                    description,
                    false
                )

                //Currently this feature is avoided, so that user can add many tasks at one time
                val logout = androidx.appcompat.app.AlertDialog.Builder(it.context)
                logout.setTitle("Update Task")
                logout.setMessage("Task already exists! Do you want to update selected task?")
                logout.setPositiveButton("Yes") { text, listener ->
                    DBAsyncTask1(
                        this,
                        taskEntity,
                        3
                    ).execute()
                    val async = DBAsyncTask1(
                        this,
                        taskEntity,
                        2
                    ).execute()
                    if (async.get()) {
                        Toast.makeText(
                            this,
                            "Task updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    }
                }
                logout.setNegativeButton("No") { text, listener ->

                }
                logout.create()
                logout.show()
            }
        }

    }

    private fun setAlarm(callback: (Long) -> Unit){
        callback(timeInMillis)
    }

    fun popTimePicker(view: View?) {
        val onTimeSetListener =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                val calendar = Calendar.getInstance()
                calendar.set(0,0,0,hour,minute,0)
                timeButton?.text = android.text.format.DateFormat.format("hh:mm aa",calendar)
            }

        // int style = AlertDialog.THEME_HOLO_DARK;
        val timePickerDialog =
            TimePickerDialog(this,  /*style,*/onTimeSetListener, hour, minute, false)
//        timePickerDialog.setTitle("Select Task Time")
        timePickerDialog.show()
    }

    private var date2 = StringBuilder()
    private fun makeDate2(scheduleDate: String): String {
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
                        day = "0${temp}"
                    }else{
                        day = temp.toString()
                    }
                }else if(count == 1){
                    month = getMonthFormat1(temp.toString()).toString()
                    if(month.toString().length < 2){
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

    class DBAsyncTask1(val context: Context, val taskEntity: TaskEntity, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, TaskDatabase::class.java, "Task-Db").build()

            when (mode) {
                1 -> {
                    val task: TaskEntity? = db.taskDao().getTaskById(taskEntity.task_id)
                    db.close()
                    return task != null
                }
                2 -> {
                    try {
                        db.taskDao().insertTask(taskEntity)
                        db.close()
                        return true
                    }catch (e: Exception){
                        return false
                    }
                }
                3 -> {
                    db.taskDao().deleteTask(taskEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, CreatePlanActivity::class.java)
        intent.putExtra("date",date.toString())
        intent.putExtra("notesDescription",notesDescription)
        intent.putExtra("title", scheduleTitle)
        startActivity(intent)
        overridePendingTransition(R.anim.pull_up_from_top,R.anim.push_out_to_bottom)
        finish()
    }
}