package com.solluzfa.solluzviewer.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.controls.NotificationManager
import com.solluzfa.solluzviewer.controls.SolluzManager
import com.solluzfa.solluzviewer.controls.SolluzService
import com.solluzfa.solluzviewer.view.detail.MainFragment
import com.solluzfa.solluzviewer.view.list.MachineListFragment


class MainActivity : AppCompatActivity(), MainFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_main, MachineListFragment.newInstance(1)).commit()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.content_main, MainFragment.newInstance(0, 1)).commit()
//        val sharedPreferences = getSharedPreferences(getString(R.string.solluz_preference_name), 0)

//        if (!sharedPreferences!!.getBoolean(getString(R.string.channel_register_pref), false)) {
            NotificationManager.getInstance().creteNotificationChannel(this)
//            sharedPreferences.edit().putBoolean(getString(R.string.channel_register_pref), true)
//                .commit()
//        }

        startForegroundService(Intent(this, SolluzService::class.java))
    }

    override fun onListFragmentInteraction(item: DataParser.Item?) {
        //TODO("not implemented") //To change body of created fnctions use File | Settings | File Templates.
    }

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.content_main)
        if ((fragment as? IOnBackPressed)?.onBackPressed() == true){
            return;
        }
        super.onBackPressed()
    }
}