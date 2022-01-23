package com.futuredeveloper.scheduleplanner.adapter

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.TaskEntity
import java.lang.String
import java.util.*


class CreatePlanAdapter(context: Context, private val itemList: List<TaskEntity>) :
    RecyclerView.Adapter<CreatePlanAdapter.CreateTaskViewHolder>() {

    class CreateTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        var itemName: TextView = view.findViewById(R.id.nameRecyclerRow)
//        var itemPrice: TextView = view.findViewById(R.id.priceRecyclerRow)
        var count: TextView = view.findViewById(R.id.count)
        var time: TextView = view.findViewById(R.id.time)
        var title: TextView = view.findViewById(R.id.title)
        var description: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateTaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.create_task_single_row, parent, false)
        return CreateTaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CreateTaskViewHolder, position: Int) {
//        holder.itemName.text = itemList[position].foodName
//        holder.itemPrice.text = "Rs. ${itemList[position].foodPrice}"
        holder.count.text = (1+position).toString()
        holder.time.text = itemList[position].taskTime
        holder.title.text = itemList[position].taskTitle
        holder.description.text = itemList[position].taskDescription
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}