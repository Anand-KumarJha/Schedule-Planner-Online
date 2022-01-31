package com.futuredeveloper.scheduleplanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.TaskEntity

class TaskRecyclerAdapter(context: Context, private val itemList: List<TaskEntity>) :
    RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskTime: TextView = view.findViewById(R.id.task_time)
        var taskCount: TextView = view.findViewById(R.id.rowCount)
        var taskName: TextView = view.findViewById(R.id.itemName)
        var taskDescription: TextView = view.findViewById(R.id.itemDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_schedule_single_row, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskTime.text = itemList[position].taskTime
        holder.taskCount.text = (1+position).toString()+ ") "

        if(itemList[position].taskTitle != "" && itemList[position].taskDescription != ""){
            holder.taskName.text = itemList[position].taskTitle
            holder.taskDescription.text = " - " + itemList[position].taskDescription
        }else{
            holder.taskName.text = itemList[position].taskTitle
            holder.taskDescription.text = itemList[position].taskDescription
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}