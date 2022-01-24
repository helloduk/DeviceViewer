package com.solluzfa.solluzviewer.controls

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.preference.PreferenceManager
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.controls.SolluzPreferenceManager.Companion.getDefaultMachine
import com.solluzfa.solluzviewer.controls.SolluzPreferenceManager.Companion.makeMachineList
import com.solluzfa.solluzviewer.controls.SolluzPreferenceManager.Companion.removeMachinePreferences
import com.solluzfa.solluzviewer.model.MachineData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class SolluzManager private constructor(val context: Context) {
    companion object {
        const val TAG = "SolluzManager"
        @Volatile
        private var instance: SolluzManager? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            SolluzManager(context).also { instance = it }
        }
    }
    val notificationManager = NotificationManager.getInstance()
    var state: Lifecycle.State = Lifecycle.State.INITIALIZED

    var machineDataList = ArrayList<MachineData>()
    val data = MutableLiveData<ArrayList<String>>().apply {
        value = ArrayList()
    }
    val push = MutableLiveData<ArrayList<String>>().apply {
        value = ArrayList()
    }
    var lastUpdateTime = ArrayList<String>()

    fun startMoritoring() =
        machineDataList.forEach { m -> m.showState(this::dataUpdated, this::pushUpdated) }

    fun stopMonitoring() = machineDataList.forEach { m -> m.clear() }

    fun updateSetting() {
        Log.i(TAG, "updateSetting: Machine count before: ${machineDataList.size}")

        stopMonitoring()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        with(machineDataList) {
            forEach { it.clear() }
            clear()
        }
        Timer().schedule(1000) {
            val machineCount = makeMachineList(context, pref, machineDataList)
            if (machineDataList.isEmpty()) {
                machineDataList.add(getDefaultMachine(context, pref, machineCount))
            }
            initData()

            startMoritoring()
            Log.i(TAG, "updateSetting: Machine count after: ${machineDataList.size}")
        }
    }

    private fun initData() {
        data.value?.clear()
        push.value?.clear()
        lastUpdateTime.clear()
        repeat(machineDataList.size) {
            data.value?.add("")
            push.value?.add("")
            lastUpdateTime.add("")
        }
    }

    private fun dataUpdated(machineID: Int, pData: String) {
        Log.i(TAG, "dataUpdated : $machineID, $pData")
        updateData(data, machineID, pData)
    }

    @SuppressLint("SimpleDateFormat")
    fun pushUpdated(machineID: Int, pPush: String) {
        Log.i(TAG, "pushUpdated : $pPush")
        val datas = pPush.split(",")
        if (datas.size < 2) {
            Log.e(TAG, "pushUpdated : wrong format - $pPush")
            updateData(push, machineID, "Wrong format")
            return
        }
        val time = datas[0].substringAfterLast(":")
        val originFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val newFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val originDate = originFormat.parse(time)
        val newDate = newFormat.format(originDate)

        val content = datas[1].substringAfterLast(":")

        updateData(push, machineID, "$newDate,$content")

        if (lastUpdateTime[machineID] != newDate) {
            updateNotification(content, newDate)
            lastUpdateTime[machineID] = newDate
        }
    }

    private fun updateNotification(content: String, newDate: String) {
        if (state == Lifecycle.State.STARTED || state == Lifecycle.State.DESTROYED) {
            notificationManager.makeNotification(context, content, newDate)
        }
    }

    private fun updateData(
        data: MutableLiveData<ArrayList<String>>,
        machineID: Int,
        value: String
    ) {
        try {
            data.value?.set(machineID, value)
        } catch (e: Exception) {
            Log.e(TAG, "updateData: $e")
        }
        data.postValue(data.value)
    }

    fun removeMachines(intArray: ArrayList<Int>) {
        removeMachinePreferences(context, machineDataList, intArray)
        updateSetting()
    }
}
