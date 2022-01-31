package com.futuredeveloper.scheduleplanner.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.futuredeveloper.scheduleplanner.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val startAct = Intent(this@SplashScreen, Login::class.java)
            startActivity(startAct)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}