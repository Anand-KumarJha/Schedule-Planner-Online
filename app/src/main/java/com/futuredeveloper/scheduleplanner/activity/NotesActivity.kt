package com.futuredeveloper.scheduleplanner.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.futuredeveloper.scheduleplanner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesActivity : AppCompatActivity() {
    private lateinit var date: String
    private lateinit var dateTextView: TextView
    private lateinit var description: EditText
    private lateinit var saveIcon: FloatingActionButton
    private var scheduleTitle = ""

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