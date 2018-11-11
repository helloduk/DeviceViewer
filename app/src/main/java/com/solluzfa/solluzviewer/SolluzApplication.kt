package com.solluzfa.solluzviewerimport android.app.Applicationimport android.content.Contextimport com.solluzfa.solluzviewer.controls.NotificationManagerclass SolluzApplication : Application() {    companion object {        private var instance:SolluzApplication? = null        fun context() : Context {            return instance!!.applicationContext        }    }    override fun onCreate() {        super.onCreate()        instance = this        // make notification channel only one time        val sharedPreferences = getSharedPreferences(getString(R.string.solluz_preference_name),0)        if(!sharedPreferences!!.getBoolean(getString(R.string.channel_register_pref), false)) {            NotificationManager.getInstance().creteNotificationChannel()            sharedPreferences.edit().putBoolean(getString(R.string.channel_register_pref), true).commit()        }    }}