package com.solluzfa.solluzviewer.controls

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.SolluzApplication
import com.solluzfa.solluzviewer.view.MainActivity


class NotificationManager private constructor() {
    companion object {
        private var instance: com.solluzfa.solluzviewer.controls.NotificationManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            NotificationManager().also { instance = it }
        }
    }

    private val pushNotificationID = 0
    private val doingNotificationID = 1

    private val CHANNEL_ID = SolluzApplication.context().packageName

    // It Should be worked only one time
    fun creteNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = CHANNEL_ID
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = SolluzApplication.context().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun makeNotification(title: String, summary: String) {
        val context = SolluzApplication.context()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        var mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_solluzfa)
                .setContentTitle(title)
                .setContentText(summary)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(pushNotificationID, mBuilder.build())
        }

    }

    fun getMonitoringNotification(): Notification {
        val context = SolluzApplication.context()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val remoteViews = RemoteViews(context.getPackageName(),
                R.layout.remoteview_layout
        ).apply {
            val intent = Intent(context, SolluzService::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getService(context, 0, intent, 0)
            setOnClickPendingIntent(R.id.cancel_monitoring, pendingIntent)
        }
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_solluzfa)
                .setContent(remoteViews)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
        return mBuilder.build()
    }
}