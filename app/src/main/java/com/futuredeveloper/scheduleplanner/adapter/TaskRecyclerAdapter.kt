package com.futuredeveloper.scheduleplanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.models.Task

class TaskRecyclerAdapter(context: Context, private val itemList: List<Task>) :
    RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var taskTime: TextView = view.findViewById(R.id.task_time)
        var taskCount: TextView = view.findViewById(R.id.rowCount)
        var taskName: TextView = view.findViewById(R.id.itemName)
        var taskDescription: TextView = view.findViewById(R.id.itemDescription)
        var taskView:View = view.findViewById(R.id.taskView)
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
        holder.taskName.text = itemList[position].taskTitle

        if(!(itemList[position].taskDescription).isNullOrEmpty()){
            holder.taskDescription.text = " - " + itemList[position].taskDescription
        }else{
            holder.taskDescription.text = ""
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}