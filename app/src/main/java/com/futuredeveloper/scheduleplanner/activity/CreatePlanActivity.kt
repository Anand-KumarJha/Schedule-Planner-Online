package com.futuredeveloper.scheduleplanner.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.adapter.CreatePlanAdapter
import com.futuredeveloper.scheduleplanner.callback.SwipeGesture
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase
import com.futuredeveloper.scheduleplanner.database.TaskDatabase
import com.futuredeveloper.scheduleplanner.database.TaskEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class CreatePlanActivity : AppCompatActivity() {
    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: CreatePlanAdapter
    private lateinit var createIcon: FloatingActionButton
    private lateinit var saveSchedule: FloatingActionButton
    private lateinit var notes: ImageView
    private lateinit var unchangedDate: String
    private lateinit var noTask: RelativeLayout
    private lateinit var nestedScrollView:NestedScrollView
    private lateinit var menuList: List<TaskEntity>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var datePickerDialog: DatePickerDialog? = null
    var dateButton: Button? = null
    lateinit var title: EditText
    var notesDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_plan)

        nestedScrollView = findViewById(R.id.nestedScrollView1)
        toolbar = findViewById(R.id.toolbar1)
        createIcon = findViewById(R.id.create_icon)
        recyclerHome = findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(this)
        saveSchedule = findViewById(R.id.save_icon)
        title = findViewById(R.id.title)
        notes = findViewById(R.id.notes)
        noTask = findViewById(R.id.noTask)

        createNotificationChannel()
        setUpToolbar()

        //Date picker
        initDatePicker()
        dateButton = findViewById(R.id.datePickerButton)
        val date = intent.getStringExtra("date")
        if(date.equals("0") || date.equals(null)){
            dateButton?.text = getTodaysDate()
        }else{
            dateButton?.text = date
        }
        unchangedDate = dateButton?.text.toString()

        if (intent.getStringExtra("title") != null){
            title.setText(intent.getStringExtra("title"))
        }else{
            title.setText(DBAsyncTask2(this, makeDate(dateButton?.text.toString())).execute().get()?.scheduleTitle)
        }

        if(intent.getStringExtra("notesDescription") != null) {
            notesDescription = intent.getStringExtra("notesDescription").toString()
        }else{
            val schedule = DBAsyncTask2(this, makeDate(dateButton?.text.toString())).execute().get()
            if(schedule != null) {
                notesDescription = schedule.scheduleDateNotes
            }
        }
        //finished
        menuList = RetrieveTaskItems(this,dateButton?.text.toString()).execute().get()

        if(menuList.isNotEmpty()){
            noTask.visibility = View.GONE
        }
        //Recycler Adapter
        recyclerHome = findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(this)
        createIcon = findViewById(R.id.create_icon)
        recyclerAdapter =
            CreatePlanAdapter(this, menuList)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager
        //finished
        ViewCompat.setNestedScrollingEnabled(recyclerHome, false)

        notes.setOnClickListener {
            val intent = Intent(this@CreatePlanActivity, NotesActivity::class.java).apply {
                putExtra("date",dateButton?.text.toString())
                putExtra("notesDescription",notesDescription)
                putExtra("title", title.text.toString())
            }
            println("notes - $notesDescription")
            startActivity(intent)
            overridePendingTransition(R.anim.pull_up_from_bottom,0)
            finish()
        }

        createIcon.setOnClickListener {
            val intent = Intent(this@CreatePlanActivity, CreateTaskActivity::class.java).apply {
                putExtra("date",dateButton?.text.toString())
                putExtra("notesDescription",notesDescription)
                putExtra("title", title.text.toString())
            }

            startActivity(intent)
            overridePendingTransition(R.anim.pull_up_from_bottom,0)
            finish()
        }

        saveSchedule.setOnClickListener {
            saveSchedule()
        }

        val swipeGesture = object : SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delete = androidx.appcompat.app.AlertDialog.Builder(this@CreatePlanActivity)
                delete.setTitle("Delete Task")
                delete.setMessage("Do you want to delete selected task?")
                delete.setPositiveButton("Yes") { text, listener ->
                    recyclerAdapter.delete(viewHolder.adapterPosition)
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
    }

    private fun saveSchedule(){
        val tasks = ArrayList<TaskEntity>()

        for(taskEntity: TaskEntity in menuList){
            val task = TaskEntity(taskEntity.task_id, taskEntity.taskTime, taskEntity.taskTitle, taskEntity.taskDescription,taskEntity.taskDone)
            tasks.add(task)
        }

        val date1 = makeDate(dateButton?.text.toString())
        val schedule = ScheduleEntity(date1,dateButton?.text.toString(),title.text.toString(),notesDescription,tasks)

        val async = DBAsyncTask1(
            this,
            schedule,
            1
        ).execute()
        val result = async.get()
        if (result) {
            Toast.makeText(
                this,
                "Schedule Updated!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                "Some error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "taskNotification"
            val description = "Channel for alarm manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("futuredeveloper.SchedulePlanner",name,importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Create Schedule"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    //For DatePicker
    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        month += 1
        val day = cal[Calendar.DAY_OF_MONTH]
        return makeDateString(day, month, year)
    }
    private fun initDatePicker() {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeDateString(day, month, year)
                dateButton?.text = date

                if(dateButton?.text.toString() != unchangedDate){
                    val refresh = Intent(this, CreatePlanActivity::class.java)
                    refresh.putExtra("date",dateButton?.text.toString())
                    startActivity(refresh)
                    finish()
                }
            }
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        val style: Int = AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
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
    fun openDatePicker(view: View?) {
        datePickerDialog?.show()
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
                        day = "0${temp}"
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

    class DBAsyncTask1(val context: Context, private val scheduleEntity: ScheduleEntity, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, ScheduleRoomDatabase::class.java, "Schedule-Db").build()

            when (mode) {
                1 -> {
                    if(db.scheduleDao().deleteIfExist(scheduleEntity.schedule_id) == 1){
                        db.scheduleDao().deleteSchedule(scheduleEntity)
                    }
                    db.scheduleDao().insertSchedule(scheduleEntity)
                    db.close()
                    return true
                }
                2 -> {
                    db.scheduleDao().deleteSchedule(scheduleEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    class DBAsyncTask2(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, ScheduleEntity>() {

        override fun doInBackground(vararg params: Void?): ScheduleEntity {
            val db = Room.databaseBuilder(context, ScheduleRoomDatabase::class.java, "Schedule-Db").build()
            val ret = db.scheduleDao().getScheduleById(id)
            db.close()
            return ret
        }
    }

    class RetrieveTaskItems(val context: Context, private val date: String) : AsyncTask<Void, Void, List<TaskEntity>>() {
        override fun doInBackground(vararg params: Void?): List<TaskEntity> {
            val db = Room.databaseBuilder(context, TaskDatabase::class.java, "Task-Db").build()
            val ret = db.taskDao().getTaskByDate(date)
            db.close()
            return ret
        }
    }

    override fun onBackPressed() {
        saveSchedule()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.pull_up_from_top,R.anim.push_out_to_bottom)
        finish()
    }
}