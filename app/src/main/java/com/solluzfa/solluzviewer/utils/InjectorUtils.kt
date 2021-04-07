package com.solluzfa.solluzviewer.utils

import com.solluzfa.solluzviewer.controls.SolluzManager
import com.solluzfa.solluzviewer.model.DataRepository
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModelFactory

object InjectorUtils {
    fun provideDeviceViewerViewModelFactory() : DeviceViewerViewModelFactory {
        val solluzManager = SolluzManager.getInstance(DataRepository.getInstance())
        return DeviceViewerViewModelFactory(solluzManager)
    }
    val STOP_SERVICE = "stop_service"
    val UPDATE_SETTINGS = "update_settings"
}