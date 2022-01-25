package com.solluzfa.solluzviewer.controls

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.utils.InjectorUtils
import kotlin.system.exitProcess

class SolluzService : Service() {
    val TAG = "SolluzService"
    private var solluzManager: SolluzManager? = null
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        solluzManager = SolluzManager.getInstance(this)
        notificationManager = NotificationManager.getInstance()
        solluzManager?.updateSetting(true)
        startForeground(1, notificationManager?.getMonitoringNotification(this))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand " + intent?.action)
        when (intent?.action) {
            InjectorUtils.STOP_SERVICE -> {
                solluzManager?.stopMonitoring()
                stopForeground(true)
                val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                sendBroadcast(closeIntent)
                stopSelf()
                android.os.Process.killProcess(android.os.Process.myPid());
                exitProcess(1);
            }
            InjectorUtils.UPDATE_SETTINGS -> {
                solluzManager?.updateSetting()
            }
            else -> {
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateSetting() {

    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(TAG, "onBind")
        return Binder()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved")
    }
}
