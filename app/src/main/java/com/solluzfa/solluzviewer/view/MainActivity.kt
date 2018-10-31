package com.solluzfa.solluzviewer.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.databinding.ActivityMainBinding
import com.solluzfa.solluzviewer.model.MachineData

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding

    private lateinit var mMachineData : MachineData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
}
