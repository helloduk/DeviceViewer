package com.solluzfa.solluzviewer.controls

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.SolluzApplication
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
                stopForeground(true)
                val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                sendBroadcast(closeIntent)
                stopSelf()
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
            InjectorUtils.UPDATE_SETTINGS -> {
                if (updateSetting()) {
                    startForeground(1, notificationManager.getMonitoringNotification())
                } else {
                    stopForeground(true)
                }
                solluzManager.startMoritoring()
            }
            else -> {
                if (updateSetting()) {
                    startForeground(1, notificationManager.getMonitoringNotification())
                } else {
                    stopForeground(true)
                }
                solluzManager.startMoritoring()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun updateSetting() : Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val bluetooth = pref.getBoolean(getString(R.string.pref_key_bluetooth), false)
        val address = pref.getString(applicationContext.getString(R.string.pref_key_url_text), "http://solluz.iptime.org/Data/")
        val code = pref.getString(applicationContext.getString(R.string.pref_key_company_code_text), "MachineData2")
        val time = pref.getString(applicationContext.getString(R.string.pref_key_interval_list), "1000").toLong()
        val push = pref.getBoolean(applicationContext.getString(R.string.pref_key_push_switch), true)

        Log.i(TAG, "updateSetting : $bluetooth $address, $code, $time, $push")
        solluzManager.updateSetting(bluetooth, address, code, time, push)
        return time != 0L
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
