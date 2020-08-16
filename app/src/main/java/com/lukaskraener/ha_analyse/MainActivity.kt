package com.lukaskraener.ha_analyse

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private val CHANEL_ID = "TEst_1234"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendnoti()
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main)

    }

    private fun sendnoti () {

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        println("drin\n\n\n\n\n\n\n\n")
        val name = "Fortschritt"
        val desp = "Zeigt den Fortschritt des uplaoder an"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val chanel = NotificationChannel(CHANEL_ID, name, importance).apply{
            description=desp
        }

        val notificationManager: NotificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(chanel)
        println("fertig\n\n\n\n\n\n\n\n")
    }
    }


}



