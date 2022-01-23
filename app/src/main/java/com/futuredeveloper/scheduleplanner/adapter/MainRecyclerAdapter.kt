package com.futuredeveloper.scheduleplanner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity

class MainRecyclerAdapter(
    val context: Context,
    private val itemList: List<ScheduleEntity>
    ) : RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder>() {

        class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            var recyclerHome: RecyclerView = view.findViewById(R.id.recyclerRecyclerView)
            lateinit var layoutManager: RecyclerView.LayoutManager
            lateinit var recyclerAdapter: TaskRecyclerAdapter

            var scheduleDate: TextView = view.findViewById(R.id.schedule_date)
            var scheduleDay:  TextView = view.findViewById(R.id.schedule_day)
            var scheduleTitle: TextView = view.findViewById(R.id.title)
            var menuButton: ImageView = view.findViewById(R.id.menu)
            val liContent: RelativeLayout = view.findViewById(R.id.relative)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.main_schedule_row, parent, false)
            return MainRecyclerAdapter.MainViewHolder(view)
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.scheduleDate.text = itemList[position].scheduleDate
            holder.scheduleTitle.text = itemList[position].scheduleTitle
            holder.scheduleDay.text = "Mon"
            holder.layoutManager = LinearLayoutManager(context)
            holder.recyclerAdapter =
                TaskRecyclerAdapter(context, itemList[position].tasks)
            holder.recyclerHome.adapter = holder.recyclerAdapter
            holder.recyclerHome.layoutManager = holder.layoutManager
            holder.menuButton.setOnClickListener {
                val intent = Intent(context, CreatePlanActivity::class.java)
                intent.putExtra("date", holder.scheduleDate.text.toString())
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
}