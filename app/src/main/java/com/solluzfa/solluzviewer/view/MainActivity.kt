package com.solluzfa.solluzviewer.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragment.OnListFragmentInteractionListener {


//    private val nameChangeObserver = Observer<String> { it?.let{device_name.text = it}}
//    private val stateChangeObserver = Observer<Boolean> {
//        it?.let{device_state.text = if(it) getText(R.string.machine_state_working)
//                                    else getText(R.string.machine_state_not_working)
//                device_state.setBackgroundColor(if(it) resources.getColor(R.color.working)
//                                            else resources.getColor(R.color.notWorking))
//        }
//    }
//    private val passedChangedObserver = Observer<Int> {
//        it?.let{passed_value.text = NumberFormat.getInstance(Locale.US).format(it)}}
//    private val failedChangedObserver = Observer<Int> {
//        it?.let{failed_value.text = NumberFormat.getInstance(Locale.US).format(it)}}
//    private val totalChangedObserver = Observer<Int> {
//        it?.let{total_value.text = NumberFormat.getInstance(Locale.US).format(it)}}
//    private val passedPercentageObserver = Observer<Float> {
//        it?.let{passed_percentage_value.text = "%.2f".format(it)}
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.content_main, MainFragment.newInstance(1)).commit()
    }

    override fun onListFragmentInteraction(item: MainFragment.Item?) {
        //TODO("not implemented") //To change body of created fnctions use File | Settings | File Templates.
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar wi7260ll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        } else super.onOptionsItemSelected(item)

    }

}
