package com.solluzfa.solluzviewer.controls

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.solluzfa.solluzviewer.utils.InjectorUtils

class SolluzService : Service() {
    val TAG = "SolluzService"
    val solluzManager = SolluzManager.getInstance()
    val notificationManager = NotificationManager.getInstance()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand " + intent!!.action)
        when(intent!!.action) {
            InjectorUtils.STOP_SERVICE -> {
                solluzManager.stopMonitoring()
                stopSelf()
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
            InjectorUtils.UPDATE_SETTINGS -> {
                solluzManager.updateSetting()
            }
            else -> {
                startForeground(1, notificationManager.getMonitoringNotification())
                solluzManager.startMoritoring()
            }
        }

        return super.onStartCommand(intent, flags, startId)
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
