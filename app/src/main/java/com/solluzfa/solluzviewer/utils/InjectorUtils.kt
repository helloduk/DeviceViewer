package com.solluzfa.solluzviewer.utils

import com.solluzfa.solluzviewer.controls.SolluzManager
import com.solluzfa.solluzviewer.model.MachineData
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModelFactory

object InjectorUtils {
    fun provideDeviceViewerViewModelFactory() : DeviceViewerViewModelFactory {
        val solluzManager = SolluzManager.getInstance(MachineData.getInstance())
        return DeviceViewerViewModelFactory(solluzManager)
    }
    val STOP_SERVICE = "stop_service"
}