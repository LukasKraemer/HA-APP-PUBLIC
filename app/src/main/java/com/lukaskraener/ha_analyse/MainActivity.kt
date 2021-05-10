package com.lukaskraener.ha_analyse


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        alarmananager()
    }
    private fun alarmananager() {

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("api_scheduled", false)
        ) {

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val notificationIntent = Intent(this, AlarmReceiver::class.java)
            val broadcast = PendingIntent.getBroadcast(
                this,
                100,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 3)
            calendar.set(Calendar.MINUTE, 0)


            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * 60 * 24,
                broadcast
            )
        }
    }
}



