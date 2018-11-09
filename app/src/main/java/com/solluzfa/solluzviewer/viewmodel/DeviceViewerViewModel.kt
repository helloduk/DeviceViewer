package com.solluzfa.solluzviewer.viewmodel

import android.arch.lifecycle.*
import android.util.Log
import com.solluzfa.solluzviewer.model.MachineData

class DeviceViewerViewModel : ViewModel(), LifecycleObserver {
    companion object {
        val TAG = DeviceViewerViewModel::class.java.simpleName
    }
    val model = MachineData
    val dataNotifier = MutableLiveData<String>()
    val pushNotifier = MutableLiveData<String>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.i(TAG,"OnCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.i(TAG,"OnResume")
        model.showState(this::dataUpdate, this::pushUpdate)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Log.i(TAG,"OnPause")
        model.clear()
    }

    fun dataUpdate(data:String) {
        Log.i(TAG,"dataUpdate : $data")
        dataNotifier.value = data
    }

    fun pushUpdate(data:String) {
        Log.i(TAG, "pushUpdate : $data")
        pushNotifier.value = data
    }
}