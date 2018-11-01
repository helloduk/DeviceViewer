package com.solluzfa.solluzviewer.viewmodel

import android.arch.lifecycle.*
import com.solluzfa.solluzviewer.model.MachineData

class DeviceViewerViewModel : ViewModel(), LifecycleObserver {
    lateinit var model: MachineData
    val nameNotifier = MutableLiveData<String>()
    val stateNotifier = MutableLiveData<Boolean>()
    val passedNotifier = MutableLiveData<Int>()
    val failedNotifier = MutableLiveData<Int>()
    val totalNotifier = MutableLiveData<Int>()
    val passedPercentageNotifier = MutableLiveData<Float>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        model = MachineData(nameNotifier, stateNotifier, passedNotifier, failedNotifier, totalNotifier, passedPercentageNotifier)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        model.showState()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        model.clear()
    }
}