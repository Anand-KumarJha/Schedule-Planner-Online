package com.futuredeveloper.scheduleplanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.futuredeveloper.scheduleplanner.R
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.database.TaskDatabase
import com.futuredeveloper.scheduleplanner.database.TaskEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.lang.String
import java.text.DateFormat
import java.util.*


class CreateTaskActivity : AppCompatActivity() {
    private var timeButton: Button? = null
    var hour = 0
    var minute:Int = 0
    lateinit var time: kotlin.String
    lateinit var title: kotlin.String
    lateinit var description: kotlin.String
    lateinit var titleEditText: EditText
    lateinit var descriptionEditText: EditText
    var timetype = "AM"
    lateinit var saveTask: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        timeButton = findViewById(R.id.timeButton)
        saveTask = findViewById(R.id.save_icon)
        titleEditText = findViewById(R.id.title)
        descriptionEditText = findViewById(R.id.description)


        saveTask.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(0,0,0,hour,minute)

            time = android.text.format.DateFormat.format("hh:mm aa",calendar).toString()
            title = titleEditText.text.toString()
            description = descriptionEditText.text.toString()

            val date = intent.getStringExtra("date")
            val taskId = date + " " + timeConversion(timeButton?.text.toString())
            println(taskId)

            val taskEntity = TaskEntity(
                taskId,
                time,
                title,
                description
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
                val intent = Intent(this@CreateTaskActivity, CreatePlanActivity::class.java)
                intent.putExtra("date",date.toString())
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Some error occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun popTimePicker(view: View?) {
        val onTimeSetListener =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                val calendar = Calendar.getInstance()
                calendar.set(0,0,0,hour,minute)

                timeButton?.setText(android.text.format.DateFormat.format("hh:mm aa",calendar))
            }

        // int style = AlertDialog.THEME_HOLO_DARK;
        val timePickerDialog =
            TimePickerDialog(this,  /*style,*/onTimeSetListener, hour, minute, false)
        timePickerDialog.setTitle("Select Task Time")
        timePickerDialog.show()
    }

    fun timeConversion(s: kotlin.String): kotlin.String? {
        var militaryTime = ""
        val hourString = s.substring(0, 2)
        val timeFormat = s.substring(6, 8)
        val timeBody = s.substring(2, 6)
        if (timeFormat == "AM") {
            militaryTime = if (hourString == "12") {
                "00$timeBody"
            } else {
                hourString + timeBody
            }
        } else if (timeFormat == "PM") {
            militaryTime = if (hourString == "12") {
                hourString + timeBody
            } else {
                val value = hourString.toInt() + 12
                value.toString() + timeBody
            }
        }
        return militaryTime
    }

    class DBAsyncTask1(val context: Context, val taskEntity: TaskEntity, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        //Mode 1: Check DB that food is favourite or not
        //Mode 2: Add to favourite
        //Mode 3: Remove from favourites

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, TaskDatabase::class.java, "Task-Db").build()

            when (mode) {
                1 -> {
                    val task: TaskEntity? = db.taskDao().getTaskById(taskEntity.task_id)
                    db.close()
                    return task != null
                }
                2 -> {
                    db.taskDao().insertTask(taskEntity)
                    db.close()
                    return true
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

}