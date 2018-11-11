package com.solluzfa.solluzviewer.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.solluzfa.solluzviewer.controls.SolluzManager

class DeviceViewerViewModelFactory (val solluzManager: SolluzManager): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeviceViewerViewModel(solluzManager) as T
    }
}