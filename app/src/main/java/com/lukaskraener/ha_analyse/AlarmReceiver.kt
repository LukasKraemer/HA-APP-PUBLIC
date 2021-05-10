package com.lukaskraener.ha_analyse

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager


class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent?) {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("api_scheduled", false)
        ) {

            try {
                API(null, context).uploader()
                sendNotification(context, context.getString(R.string.uploadfinish))
                API(null, context).programmstart()
                sendNotification(context, context.getString(R.string.values_calculated), 1)
            } catch (e: Exception) {
                sendNotification(context, context.getString(R.string.scheduled_error))
            }

        }
    }

    fun sendNotification(context: Context, contenttext:String, id:Int=0){

        val notificationIntent = Intent(context, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(notificationIntent)
        val pendingIntent: PendingIntent =
            stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder: Notification.Builder = Notification.Builder(context)
        val notification: Notification = builder.setContentTitle( context.getString(R.string.app_name))
            .setContentText(contenttext)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID)
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id, notification)
    }
    companion object {
        private const val CHANNEL_ID = "com.lukaskraener.ha_analyse.channelId"
    }

}