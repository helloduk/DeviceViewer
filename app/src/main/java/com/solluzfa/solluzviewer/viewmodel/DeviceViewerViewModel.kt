package com.solluzfa.solluzviewer.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.controls.SolluzManager

class DeviceViewerViewModel(private val solluzManager: SolluzManager) : ViewModel(),
    LifecycleObserver {
    companion object {
        val TAG = DeviceViewerViewModel::class.java.simpleName
    }

    fun getData() = solluzManager.data
    fun getPush() = solluzManager.push

    fun removeMachines(intArray: ArrayList<Int>) {
        solluzManager.removeMachines(intArray)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.i(TAG, "OnCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.i(TAG, "OnResume")
        solluzManager.state = Lifecycle.State.RESUMED
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Log.i(TAG, "OnPause")
        solluzManager.state = Lifecycle.State.STARTED
    }

    override fun onCleared() {
        super.onCleared()
        solluzManager.state = Lifecycle.State.DESTROYED
    }
}