package com.solluzfa.solluzviewer.controls

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.solluzfa.solluzviewer.SolluzApplication
import com.solluzfa.solluzviewer.model.MachineData
import java.text.SimpleDateFormat

class SolluzManager private constructor(val machineData: MachineData) {
    companion object {
        @Volatile private var instance : SolluzManager? = null
        fun getInstance(machineData: MachineData) = instance ?: synchronized(this){
            SolluzManager(machineData).also { instance = it }
        }
        fun getInstance() = instance ?: synchronized(this) {
            SolluzManager(MachineData.getInstance()).also { instance = it }
        }
        val TAG = "SolluzManager"
    }

    val notificationManager = NotificationManager.getInstance()
    var state : Lifecycle.State = Lifecycle.State.INITIALIZED

    fun startMoritoring() = machineData.showState(this::dataUpdated, this::pushUpdated)
    fun stopMonitoring() = machineData.clear()
    fun updateSetting() {
        val pref = PreferenceManager.getDefaultSharedPreferences(SolluzApplication.context())
        val address = pref.getString("pref_key_url_text", "http://solluz.iptime.org/Data/")
        val code = pref.getString("pref_key_company_code_text", "MachineData2")
        val time = pref.getString("pref_key_interval_list", "1000").toLong()
        val push = pref.getBoolean("pref_key_push_switch", true)

        Log.i(TAG, "updateSetting : $address, $code, $time, $push")
        machineData.updateSetting(address,code,time,push)
        startMoritoring()
    }

    val data = MutableLiveData<String>()
    val push = MutableLiveData<String>()
    var lastUpdateTime = ""


    fun dataUpdated(pData : String) {
        Log.i(TAG, "dataUpdated : $pData")
        data.value = pData
    }

    fun pushUpdated(pPush : String) {
        Log.i(TAG, "pushUpdated : $pPush")
        val datas = pPush.split(",")
        if (datas.size < 2) {
            Log.e(TAG, "pushUpdated : wrong format - $pPush")
            return
        }
        val time = datas[0].substringAfterLast(":")
        val originFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val newFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val originDate = originFormat.parse(time)
        val newDate = newFormat.format(originDate)

        val content = datas[1].substringAfterLast(":")
        push.value = "$newDate,$content"
        if(lastUpdateTime != newDate) {
            if (state == Lifecycle.State.STARTED || state == Lifecycle.State.DESTROYED) {
                    notificationManager.makeNotification(content, newDate)
            }
            lastUpdateTime = newDate
        }
    }
}
