package com.futuredeveloper.scheduleplanner.activity

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.ScheduleEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesActivity : AppCompatActivity() {
    lateinit var date: String
    lateinit var dateTextView: TextView
    lateinit var description: EditText
    lateinit var saveIcon: FloatingActionButton
    var scheduleTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        date = intent.getStringExtra("date").toString()
        val notesDescription = intent.getStringExtra("notesDescription").toString()
        scheduleTitle = intent.getStringExtra("title").toString()

        dateTextView = findViewById(R.id.date)
        dateTextView.text = date
        description = findViewById(R.id.description)
        saveIcon = findViewById(R.id.save_icon)

        description.setText(notesDescription)

        saveIcon.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, CreatePlanActivity::class.java)
        intent.putExtra("date",date)
        intent.putExtra("notesDescription",description.text.toString())
        intent.putExtra("title", scheduleTitle)
        startActivity(intent)
        overridePendingTransition(R.anim.pull_up_from_top,R.anim.push_out_to_bottom)
        Toast.makeText(this, "Notes Added, Save Schedule to Save Notes", Toast.LENGTH_LONG).show()
        finish()
    }
}