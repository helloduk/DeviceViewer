package com.solluzfa.solluzviewer.utils

import android.content.Context
import com.solluzfa.solluzviewer.controls.SolluzManager
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModelFactory

object InjectorUtils {
    fun provideDeviceViewerViewModelFactory(context: Context): DeviceViewerViewModelFactory {
        val solluzManager = SolluzManager.getInstance(context)
        return DeviceViewerViewModelFactory(solluzManager)
    }

    const val STOP_SERVICE = "stop_service"
    const val UPDATE_SETTINGS = "update_settings"

    const val EXTRA_KEY_MACHINE_ID = "machine_id"
}