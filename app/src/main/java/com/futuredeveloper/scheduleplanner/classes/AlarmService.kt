package com.futuredeveloper.scheduleplanner.classes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import com.futuredeveloper.scheduleplanner.util.Constants

class AlarmService(private val context: Context, private val requestCode: Int, private val message: String) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    fun setExactAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = Constants.ACTION_SET_EXACT
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                    putExtra("message", message)
                },requestCode
            )
        )
    }

    //1 Week
    fun setRepetitiveAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = Constants.ACTION_SET_REPETITIVE_EXACT
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                    putExtra("message", message)
                    putExtra("requestCode",requestCode)
                },requestCode
            )
        )
    }

    private fun getPendingIntent(intent: Intent,requestCode: Int) =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


    fun cancelAlarm(timeInMillis: Long){
        alarmManager?.cancel(getPendingIntent(
            getIntent().apply {
                action = Constants.ACTION_SET_EXACT
                putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
            },requestCode
        ))
    }

    fun cancelRepeatAlarm(timeInMillis: Long){
        alarmManager?.cancel(getPendingIntent(
            getIntent().apply {
                action = Constants.ACTION_SET_REPETITIVE_EXACT
                putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
            },requestCode
        ))
    }

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getIntent() = Intent(context, AlarmReceiver::class.java)
}