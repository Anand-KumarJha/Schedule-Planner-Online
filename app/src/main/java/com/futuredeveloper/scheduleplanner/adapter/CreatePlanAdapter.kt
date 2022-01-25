package com.futuredeveloper.scheduleplanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.TaskEntity


class CreatePlanAdapter(context: Context, private val itemList: List<TaskEntity>) :
    RecyclerView.Adapter<CreatePlanAdapter.CreateTaskViewHolder>() {

    class CreateTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var count: TextView = view.findViewById(R.id.count)
        var time: TextView = view.findViewById(R.id.time)
        var title: TextView = view.findViewById(R.id.title)
        var description: TextView = view.findViewById(R.id.description)
        var editButton: ImageView = view.findViewById(R.id.edit2)
        var deleteButton: ImageView = view.findViewById(R.id.delete2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateTaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.create_task_single_row, parent, false)
        return CreateTaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CreateTaskViewHolder, position: Int) {

        holder.count.text = (1+position).toString()
        holder.time.text = itemList[position].taskTime
        holder.title.text = itemList[position].taskTitle
        holder.description.text = itemList[position].taskDescription

        holder.editButton.setOnClickListener{
            Toast.makeText(it.context,"Clicked Edit", Toast.LENGTH_SHORT).show()
        }
        holder.deleteButton.setOnClickListener {
            val logout = androidx.appcompat.app.AlertDialog.Builder(it.context)
            logout.setTitle("Delete Task")
            logout.setMessage("Do you want to delete selected task?")
            logout.setPositiveButton("Yes") { text, listener ->
                val async2 = MainRecyclerAdapter.DBAsyncTask2(
                    it.context,
                    itemList[position].task_id
                ).execute()
                (it.getContext() as android.app.Activity?)?.recreate()

                Toast.makeText(it.context,"Task Deleted", Toast.LENGTH_SHORT).show()
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


    class DBAsyncTask2(val context: Context, val id: String) :
        android.os.AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = androidx.room.Room.databaseBuilder(context, com.futuredeveloper.scheduleplanner.database.TaskDatabase::class.java, "Task-Db").build()

            db.taskDao().deleteTaskById(id.toString())
            db.close()
            return true
        }
    }
}