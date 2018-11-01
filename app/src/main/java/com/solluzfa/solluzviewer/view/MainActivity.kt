package com.solluzfa.solluzviewer.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mViewModel : DeviceViewerViewModel by lazy {
        ViewModelProviders.of(this).get(DeviceViewerViewModel::class.java)
    }

    private val nameChangeObserver = Observer<String> { it?.let{device_name.text = it}}
    private val stateChangeObserver = Observer<Boolean> {
        it?.let{device_state.text = if(it) getText(R.string.machine_state_working)
                                    else getText(R.string.machine_state_not_working)
                device_state.setBackgroundColor(if(it) resources.getColor(R.color.working)
                                            else resources.getColor(R.color.notWorking))
        }
    }
    private val passedChangedObserver = Observer<Int> {
        it?.let{passed_value.text = NumberFormat.getInstance(Locale.US).format(it)}}
    private val failedChangedObserver = Observer<Int> {
        it?.let{failed_value.text = NumberFormat.getInstance(Locale.US).format(it)}}
    private val totalChangedObserver = Observer<Int> {
        it?.let{total_value.text = NumberFormat.getInstance(Locale.US).format(it)}}
    private val passedPercentageObserver = Observer<Float> {
        it?.let{passed_percentage_value.text = "%.2f".format(it)}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(mViewModel)
        mViewModel.nameNotifier.observe(this, nameChangeObserver)
        mViewModel.stateNotifier.observe(this, stateChangeObserver)
        mViewModel.passedNotifier.observe(this, passedChangedObserver)
        mViewModel.failedNotifier.observe(this, failedChangedObserver)
        mViewModel.totalNotifier.observe(this, totalChangedObserver)
        mViewModel.passedPercentageNotifier.observe(this, passedPercentageObserver)
    }
}
