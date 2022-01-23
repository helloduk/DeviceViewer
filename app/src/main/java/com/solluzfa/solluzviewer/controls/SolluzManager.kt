package com.solluzfa.solluzviewer.controls

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.model.MachineData
import java.text.SimpleDateFormat

class SolluzManager {
    companion object {
        @Volatile
        private var instance: SolluzManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            SolluzManager().also { instance = it }
        }

        val TAG = "SolluzManager"
    }

    val notificationManager = NotificationManager.getInstance()
    var state: Lifecycle.State = Lifecycle.State.INITIALIZED

    fun startMoritoring() =
        machineDataList.forEach { m -> m.showState(this::dataUpdated, this::pushUpdated) }

    fun stopMonitoring() = machineDataList.forEach { m -> m.clear() }

    fun updateSetting(applicationContext: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        var machineCount = 0

        with(machineDataList) {
            forEach { it.clear() }
            clear()
        }

        while (true) {
            val address = pref.getString(
                applicationContext.getString(R.string.pref_key_url_text) + machineCount,
                ""
            )
            val code = pref.getString(
                applicationContext.getString(R.string.pref_key_company_code_text) + machineCount,
                ""
            )
            val time = pref.getString(
                applicationContext.getString(R.string.pref_key_interval_list) + machineCount,
                "1000"
            )?.toLongOrNull() ?: 1000
            val push = pref.getBoolean(
                applicationContext.getString(R.string.pref_key_push_switch) + machineCount,
                true
            )

            Log.i(TAG, "address: $address, code: $code, time: $time, push: $push")

            if (address?.isEmpty() != false) {
                break;
            } else {
                machineDataList.add(
                    machineCount,
                    MachineData(machineCount).apply { updateSetting(address, code, time, push) })
                machineCount++
            }
        }

        if (machineDataList.isEmpty()) {
            setDefaultMachine(applicationContext, pref, machineCount)
        }

        initData()
        Log.i(TAG, "updateSetting: Machine count: ${machineDataList.size}")
        return true
    }

    private fun initData() {
        data.value = ArrayList()
        push.value = ArrayList()
        repeat(machineDataList.size) {
            data.value?.add("")
            push.value?.add("")
        }
    }

    private fun setDefaultMachine(
        applicationContext: Context,
        pref: SharedPreferences,
        machineCount: Int
    ) {
        with(pref.edit()) {
            putString(
                applicationContext.getString(R.string.pref_key_url_text) + machineCount,
                "http://solluz.iptime.org/Data/"
            )
            putString(
                applicationContext.getString(R.string.pref_key_company_code_text) + machineCount,
                "MachineData2"
            )
            putString(
                applicationContext.getString(R.string.pref_key_interval_list) + machineCount,
                "1000"
            )
            putBoolean(
                applicationContext.getString(R.string.pref_key_push_switch) + machineCount,
                true
            )
            apply()
        }

        machineDataList.add(MachineData(machineCount).apply {
            updateSetting(
                "http://solluz.iptime.org/Data/",
                "MachineData2",
                1000L,
                true
            )
        })
    }

    var machineDataList = ArrayList<MachineData>()
    val data = MutableLiveData<ArrayList<String>>()
    val push = MutableLiveData<ArrayList<String>>()
    var lastUpdateTime = ArrayList<String>()

    fun dataUpdated(machineID: Int, pData: String) {
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

        if (lastUpdateTime.lastIndex < machineID) {
            updateNotification(content, newDate)
            lastUpdateTime.add(machineID, newDate)
        } else if (lastUpdateTime[machineID] != newDate) {
            updateNotification(content, newDate)
            lastUpdateTime[machineID] = newDate
        }
    }

    private fun updateNotification(content: String, newDate: String) {
        if (state == Lifecycle.State.STARTED || state == Lifecycle.State.DESTROYED) {
            notificationManager.makeNotification(content, newDate)
        }
    }

    private fun updateData(
        data: MutableLiveData<ArrayList<String>>,
        machineID: Int,
        value: String
    ) {
        data.value?.set(machineID, value)
        data.postValue(data.value)
    }

    fun removeMachines(context: Context, intArray: ArrayList<Int>) {
        stopMonitoring()
        var currentMachineIndex = 0

        intArray.forEachIndexed { index, machineID ->
            currentMachineIndex = if (index > currentMachineIndex) index else currentMachineIndex

            for(i in currentMachineIndex .. machineDataList.lastIndex) {
                if(intArray.contains(i))
                    currentMachineIndex++
                else
                    break;
            }
            if (currentMachineIndex <= machineDataList.lastIndex) {
                movePreference(context, currentMachineIndex, index)
            }
        }

        for(i in machineDataList.lastIndex downTo machineDataList.size - intArray.size) {
            removePreference(context, i)
        }
        startMoritoring()
    }

    @SuppressLint("ApplySharedPref")
    private fun movePreference(context: Context, from: Int, to: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit()?.apply {
            putString(context.getString(R.string.pref_key_url_text) + to,
                pref.getString(context.getString(R.string.pref_key_url_text) + from, ""))
            putString(context.getString(R.string.pref_key_company_code_text) + to,
                pref.getString(context.getString(R.string.pref_key_company_code_text) + from, ""))
            putString(context.getString(R.string.pref_key_interval_list) + to,
                pref.getString(context.getString(R.string.pref_key_interval_list) + from, ""))
            putBoolean(context.getString(R.string.pref_key_push_switch) + to,
                pref.getBoolean(context.getString(R.string.pref_key_push_switch) + from, true))
            commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun removePreference(context: Context, machineID: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        pref.edit()?.apply {
            remove(context.getString(R.string.pref_key_url_text) + machineID)
            remove(context.getString(R.string.pref_key_company_code_text) + machineID)
            remove(context.getString(R.string.pref_key_interval_list) + machineID)
            remove(context.getString(R.string.pref_key_push_switch) + machineID)
            commit()
        }
    }
}
