package com.solluzfa.solluzviewer

import AppLifecycle
import android.app.Application

class SolluzApplication : Application() {
    companion object {
        private var instance: SolluzApplication? = null
        fun getInstance(): SolluzApplication {
            return instance!!
        }
    }

    private val lifeCycle = AppLifecycle()

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(lifeCycle)
    }

    fun isActivityShowing(): Boolean = lifeCycle.isShowing
}