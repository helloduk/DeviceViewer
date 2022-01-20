package com.solluzfa.solluzviewer.utils

import com.solluzfa.solluzviewer.controls.SolluzManager
import com.solluzfa.solluzviewer.model.MachineData
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModelFactory

object InjectorUtils {
    fun provideDeviceViewerViewModelFactory() : DeviceViewerViewModelFactory {
        val solluzManager = SolluzManager.getInstance()
        return DeviceViewerViewModelFactory(solluzManager)
    }

    const val STOP_SERVICE = "stop_service"
    const val UPDATE_SETTINGS = "update_settings"

    const val EXTRA_KEY_MACHINE_ID = "machine_id"
}