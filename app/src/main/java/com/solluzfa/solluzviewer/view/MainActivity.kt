package com.solluzfa.solluzviewer.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.controls.SolluzService


class MainActivity : AppCompatActivity(), MainFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_main, MachineListFragment.newInstance(1)).commit()
        startService(Intent(this, SolluzService::class.java))
    }

    override fun onListFragmentInteraction(item: DataParser.Item?) {
        //TODO("not implemented") //To change body of created fnctions use File | Settings | File Templates.
    }
}