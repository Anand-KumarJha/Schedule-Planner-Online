package com.futuredeveloper.scheduleplanner.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.activity.CreateTaskActivity
import com.futuredeveloper.scheduleplanner.classes.AlarmService
import com.futuredeveloper.scheduleplanner.database.TaskEntity


class CreatePlanAdapter(val context: Context, private val itemList: List<TaskEntity>) :
    RecyclerView.Adapter<CreatePlanAdapter.CreateTaskViewHolder>() {

    private var timeInMillis: Long = 0

    class CreateTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var count: TextView = view.findViewById(R.id.count)
        var time: TextView = view.findViewById(R.id.time)
        var title: TextView = view.findViewById(R.id.title)
        var description: TextView = view.findViewById(R.id.description)
        var taskDone: ImageView = view.findViewById(R.id.done)
        var deleteButton: ImageView = view.findViewById(R.id.delete2)
        var todayDone: ImageView = view.findViewById(R.id.today_done)
        var editButton: ImageView = view.findViewById(R.id.edit2)


        var intentDate = ""
        var intentNotesDescription = ""
        var intentTitle = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateTaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.create_task_single_row, parent, false)
        return CreateTaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CreateTaskViewHolder, position: Int) {

        var count = 0
        var start = 0
        val sb = StringBuilder()
        for(i in (itemList[position].task_id)){
            if(i == ','){
                count++
            }
            if(count >= 2){
                break
            }
            if(count == 1 && start > 0){
                sb.append(i)
            }
            if(count == 1){
                start++
            }
        }

        timeInMillis = (sb.toString()).toLong()
        if(context is CreatePlanActivity){
            holder.intentDate = context.dateButton?.text.toString()
            holder.intentNotesDescription = context.notesDescription
            holder.intentTitle = context.title.text.toString()

            holder.todayDone.visibility = View.GONE
            holder.deleteButton.visibility = View.VISIBLE
            holder.taskDone.visibility = View.VISIBLE
            holder.editButton.visibility = View.VISIBLE
            holder.taskDone.setImageResource(R.drawable.actionbar_not_done_task)
            holder.deleteButton.setImageResource(R.drawable.actionbar_delete_task)

            if(itemList[position].taskDone){
                holder.taskDone.setImageResource(R.drawable.actionbar_done_task)
            }

            holder.taskDone.setOnClickListener{
                val taskEntity= GetTaskByIds(context,itemList[position].task_id).execute().get()

                if(taskEntity.taskDone){
                    holder.taskDone.setImageResource(R.drawable.actionbar_not_done_task)
                    UpdateTaskById(context,itemList[position].task_id).execute()
                    Toast.makeText(it.context,"Task Not Completed", Toast.LENGTH_SHORT).show()
                }else{
                    holder.taskDone.setImageResource(R.drawable.actionbar_done_task)
                    UpdateTaskById(context,itemList[position].task_id).execute()
                    Toast.makeText(it.context,"Task Completed", Toast.LENGTH_SHORT).show()
                }
                (context as Activity).recreate()
            }
            holder.editButton.setOnClickListener {
                val intent = Intent(context, CreateTaskActivity::class.java).apply {
                    putExtra("date",holder.intentDate)
                    putExtra("notesDescription",holder.intentNotesDescription)
                    putExtra("title", holder.intentTitle)
                    putExtra("taskId",itemList[position].task_id)
                    putExtra("taskTime",itemList[position].taskTime)
                    putExtra("taskTitle",itemList[position].taskTitle)
                    putExtra("taskDescription",itemList[position].taskDescription)
                }

                (context as Activity).startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.pull_up_from_bottom,0)
                (context as Activity).finish()
            }
            holder.deleteButton.setOnClickListener {
                val logout = androidx.appcompat.app.AlertDialog.Builder(it.context)
                logout.setTitle("Delete Task")
                logout.setMessage("Do you want to delete selected task?")
                logout.setPositiveButton("Yes") { text, listener ->
                    delete(position)
                }
                logout.setNegativeButton("No") { text, listener ->

                }
                logout.create()
                logout.show()
            }
        }else{
            if(itemList[position].taskDone){
                holder.todayDone.visibility = View.VISIBLE
                holder.todayDone.setImageResource(R.drawable.actionbar_done_task)
            }else{
                holder.todayDone.visibility = View.VISIBLE
            }
        }

        holder.count.text = (1+position).toString()
        holder.time.text = itemList[position].taskTime
        holder.title.text = itemList[position].taskTitle
        holder.description.text = itemList[position].taskDescription
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun delete(position: Int){
        val sb = StringBuilder()
        var count = 0
        for(i in itemList[position].task_id.toString()){
            if(count >= 2){
                sb.append(i)
            }
            if(i == ',')count++
        }
        val alarmNo = Integer.parseInt(sb.toString())

        val alarmService = AlarmService(context as Activity, alarmNo, "")
        println("Canceled alarm ----------------$alarmNo")
        cancelAlarm{alarmService.cancelAlarm(timeInMillis) }

        DBAsyncTask2(
            context,
            itemList[position].task_id
        ).execute()

        (context as Activity?)?.recreate()
        Toast.makeText(context,"Task Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(callback: (Long) -> Unit){
        callback(timeInMillis)
    }

    class DBAsyncTask2(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.TaskDatabase::class.java, "Task-Db").build()

            db.taskDao().deleteTaskById(id)
            db.close()
            return true
        }
    }

    class UpdateTaskById(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.TaskDatabase::class.java, "Task-Db").build()

            val taskEntity = db.taskDao().getTaskById(id)
            taskEntity.taskDone = !(taskEntity.taskDone)

            db.taskDao().updateTask(taskEntity)
            db.close()
            return true
        }
    }

    class GetTaskByIds(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, TaskEntity>() {

        override fun doInBackground(vararg params: Void?): TaskEntity{
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.TaskDatabase::class.java, "Task-Db").build()

            val taskEntity:TaskEntity = db.taskDao().getTaskById(id)
            db.close()
            return taskEntity
        }
    }
}