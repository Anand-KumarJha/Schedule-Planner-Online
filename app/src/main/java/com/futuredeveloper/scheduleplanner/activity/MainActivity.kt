package com.futuredeveloper.scheduleplanner.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.futuredeveloper.scheduleplanner.BuildConfig
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.fragment.AboutFragment
import com.futuredeveloper.scheduleplanner.fragment.HomeFragment
import com.futuredeveloper.scheduleplanner.fragment.NotesFragment
import com.futuredeveloper.scheduleplanner.fragment.PastScheduleFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var frameLayout: FrameLayout
    private var reviewInfo: ReviewInfo? = null
    private var manager: ReviewManager? = null
    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinator)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        frameLayout = findViewById(R.id.frame)

        setUpToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //Drawer Menu Clicks Handling
        navigationView.setNavigationItemSelectedListener {
            //Highlighting the selected item
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.notes -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, NotesFragment()).commit()
                    supportActionBar?.title = "Notes"
                    drawerLayout.closeDrawers()
                }
                R.id.past_schedules -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, PastScheduleFragment()).commit()
                    supportActionBar?.title = "Past Schedules"
                    drawerLayout.closeDrawers()
                }
                R.id.shareApp -> {
                    val delete = androidx.appcompat.app.AlertDialog.Builder(this)
                    delete.setTitle("Share App")
                    delete.setMessage("How do you want to share the app?")
                    delete.setPositiveButton("Link") { text, listener ->
                        try{
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.setType("text/plain")
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Schedule your all tasks with this amazing app");
                            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n");
                            startActivity(Intent.createChooser(intent,"Share With"))
                        }catch (e: Exception){}
                    }
                    delete.setNegativeButton("APK") { text, listener ->
                        val api = applicationContext.applicationInfo
                        val apkPath = api.sourceDir
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "application/vnd.android.package-archive"
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(apkPath)))
                        intent.putExtra(Intent.EXTRA_TEXT, "Schedule your all tasks with this amazing app");
                        startActivity(Intent.createChooser(intent,"Share With"))
                    }
                    delete.create()
                    delete.show()
                }
                R.id.giveRating -> {
                    try{
                        val uri = Uri.parse("market://details?id=$packageName")
                        val intent = Intent(Intent.ACTION_VIEW,uri)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }catch (e: Exception){
                        val uri = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                        val intent = Intent(Intent.ACTION_VIEW,uri)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }
                R.id.about -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, AboutFragment()).commit()
                    supportActionBar?.title = "About App"
                    drawerLayout.closeDrawers()
                }

            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, HomeFragment()).commit()
        supportActionBar?.title = "Schedule Planner"
        navigationView.setCheckedItem(R.id.home)
        drawerLayout.closeDrawers()

    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Schedule Planner"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }

    }
}