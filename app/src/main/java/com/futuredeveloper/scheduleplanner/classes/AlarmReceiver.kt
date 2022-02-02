package com.futuredeveloper.scheduleplanner.classes

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.activity.CreatePlanActivity
import com.futuredeveloper.scheduleplanner.util.Constants
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class AlarmReceiver: BroadcastReceiver() {

    private var requestCode = 0
    private var timeInMillis: Long? = 0L
    override fun onReceive(context: Context, intent: Intent?) {
        timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        val message = intent?.getStringExtra("message")

        when (intent?.action) {
            Constants.ACTION_SET_EXACT -> {
                buildNotification(context, "Task is Scheduled", message.toString())
            }
            Constants.ACTION_SET_REPETITIVE_EXACT -> {
                requestCode = intent.getIntExtra("requestCode",0)
                val alarmReceiver = AlarmService(context,requestCode,message.toString())
                println("Alarm created - $requestCode")
                setRepetitiveAlarm(alarmReceiver)
                buildNotification(context, "Task is Scheduled", message.toString())
            }
        }
    }

    private fun buildNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, CreatePlanActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE, "appname::WakeLock"
        )
        wakeLock.acquire(1 * 60 * 1000L)

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(2000)
        }

        try {
            val rawPathUri: Uri = Uri.parse("android.resource://com.futuredeveloper.scheduleplanner" + "/" + R.raw.notify);
            val r = RingtoneManager.getRingtone(context, rawPathUri)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val builder = NotificationCompat.Builder(context, "futuredeveloper.SchedulePlanner")
            .setSmallIcon(R.drawable.actionbar_not_done_task)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123,builder.build())
    }

    private fun setRepetitiveAlarm(alarmService: AlarmService) {
        val timeInMillis2 = timeInMillis
        val cal = Calendar.getInstance().apply {
            if (timeInMillis2 != null) {
                this.timeInMillis = timeInMillis2 + TimeUnit.DAYS.toMillis(1)
            }
        }
        println(cal.timeInMillis)
        alarmService.setRepetitiveAlarm(cal.timeInMillis)
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()

}