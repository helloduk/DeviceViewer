package com.solluzfa.solluzviewer.controls

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.utils.InjectorUtils
import com.solluzfa.solluzviewer.view.MainActivity


class NotificationManager private constructor() {
    companion object {
        private const val TAG = "NotificationManager"

        private var instance: com.solluzfa.solluzviewer.controls.NotificationManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            NotificationManager().also { instance = it }
        }
    }

    private val pushNotificationID = 0

    private val CHANNEL_ID = "com.solluzfa.solluzviewer"

    // It Should be worked only one time
    fun creteNotificationChannel(context: Context) {
        val name = CHANNEL_ID
        val descriptionText = CHANNEL_ID
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
//                setSound(null, null)
        }

        Log.i(TAG, "createNotificationChannel: ")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun makeNotification(context: Context, title: String, summary: String) {
        Log.i(TAG, "makeNotification: title($title), summary($summary) ")

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

    fun getMonitoringNotification(context: Context): Notification {
        Log.i(TAG, "getMonitoringNotification:")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val remoteViews = RemoteViews(
            context.packageName,
            R.layout.remoteview_layout
        ).apply {
            val intent = Intent(context, SolluzService::class.java)
            intent.action = InjectorUtils.STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(context, 0, intent, 0)
            setOnClickPendingIntent(R.id.cancel_monitoring, pendingIntent)
        }
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_solluzfa)
            .setContent(remoteViews)
            .setSound(null)
            .setContentIntent(pendingIntent)
        return mBuilder.build()
    }
}