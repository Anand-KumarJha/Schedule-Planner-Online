package com.futuredeveloper.scheduleplanner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.adapter.MainRecyclerAdapter
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import com.futuredeveloper.scheduleplanner.database.ScheduleRoomDatabase
import com.futuredeveloper.scheduleplanner.database.TaskDatabase
import com.futuredeveloper.scheduleplanner.database.TaskEntity
import com.futuredeveloper.scheduleplanner.models.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the com.example.scheduleplanner.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this com.example.scheduleplanner.fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: MainRecyclerAdapter
    private lateinit var createIcon: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this com.example.scheduleplanner.fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        createIcon = view.findViewById(R.id.create_icon)

        val scheduleList = HomeFragment.RetrieveCartItems(activity as Context).execute().get()
        recyclerAdapter =
            MainRecyclerAdapter(activity as Context, scheduleList)

        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager


        createIcon.setOnClickListener {
            val intent = Intent(context, CreatePlanActivity::class.java)
            intent.putExtra("date","0")
            startActivity(intent)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this com.example.scheduleplanner.fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of com.example.scheduleplanner.fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    class RetrieveCartItems(val context: Context) : AsyncTask<Void, Void, List<ScheduleEntity>>() {
        override fun doInBackground(vararg params: Void?): List<ScheduleEntity>? {
            val db = Room.databaseBuilder(context, ScheduleRoomDatabase::class.java, "Schedule-Db").build()
            val ret = db.scheduleDao().getAllSchedule()
            db.close()
            return ret
        }

    }
}