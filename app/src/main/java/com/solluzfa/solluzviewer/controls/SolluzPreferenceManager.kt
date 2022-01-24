package com.solluzfa.solluzviewer.controls

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.model.MachineData

class SolluzPreferenceManager {
    companion object {
        fun removeMachinePreferences(
            context: Context,
            machineDataList: ArrayList<MachineData>,
            removeIdArray: ArrayList<Int>
        ) {
            val resultList = ArrayList<Int>()
            for (i in 0 until machineDataList.size) {
                if (!removeIdArray.contains(i)) {
                    resultList.add(i)
                }
            }
            for (i in 0 until resultList.size) {
                movePreference(context, resultList[i], i)
            }

            for (i in machineDataList.lastIndex downTo machineDataList.size - removeIdArray.size) {
                removePreference(context, i)
            }
        }

        @SuppressLint("ApplySharedPref")
        private fun movePreference(context: Context, from: Int, to: Int) {
            Log.i(SolluzManager.TAG, "movePreference from($from), to($to)")

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            pref.edit()?.apply {
                putString(
                    context.getString(R.string.pref_key_url_text) + to,
                    pref.getString(context.getString(R.string.pref_key_url_text) + from, "")
                )
                putString(
                    context.getString(R.string.pref_key_company_code_text) + to,
                    pref.getString(
                        context.getString(R.string.pref_key_company_code_text) + from,
                        ""
                    )
                )
                putString(
                    context.getString(R.string.pref_key_interval_list) + to,
                    pref.getString(context.getString(R.string.pref_key_interval_list) + from, "")
                )
                putBoolean(
                    context.getString(R.string.pref_key_push_switch) + to,
                    pref.getBoolean(context.getString(R.string.pref_key_push_switch) + from, true)
                )
                commit()
            }
        }

        @SuppressLint("ApplySharedPref")
        private fun removePreference(context: Context, machineID: Int) {
            Log.i(SolluzManager.TAG, "removePreference machineID($machineID)")
            val pref = PreferenceManager.getDefaultSharedPreferences(context)

            pref.edit()?.apply {
                remove(context.getString(R.string.pref_key_url_text) + machineID)
                remove(context.getString(R.string.pref_key_company_code_text) + machineID)
                remove(context.getString(R.string.pref_key_interval_list) + machineID)
                remove(context.getString(R.string.pref_key_push_switch) + machineID)
                commit()
            }
        }

        fun makeMachineList(
            applicationContext: Context,
            pref: SharedPreferences,
            machineDataList: ArrayList<MachineData>
        ): Int {
            var machineCount = 0
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

                Log.i(SolluzManager.TAG, "address: $address, code: $code, time: $time, push: $push")

                if (address?.isEmpty() != false) {
                    break;
                } else {
                    machineDataList.add(
                        machineCount,
                        MachineData(machineCount).apply { updateSetting(address, code, time, push) })
                    machineCount++
                }
            }
            return machineCount
        }

        fun getDefaultMachine(
            applicationContext: Context,
            pref: SharedPreferences,
            machineCount: Int
        ): MachineData {
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

            return MachineData(machineCount).apply {
                updateSetting(
                    "http://solluz.iptime.org/Data/",
                    "MachineData2",
                    1000L,
                    true
                )
            }
        }
    }
}