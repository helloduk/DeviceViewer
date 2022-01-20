package com.solluzfa.solluzviewer.view

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.utils.InjectorUtils
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModel
import kotlinx.android.synthetic.main.captionlist_layout.*
import kotlinx.android.synthetic.main.captionlist_layout.view.*
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [MainFragment.OnListFragmentInteractionListener] interface.
 */
class MainFragment : Fragment() {
    private val TAG = "MainFragment"
    private var columnCount = 1
    private var machineID = 0
    private var listener: OnListFragmentInteractionListener? = null
    private val items: MutableList<DataParser.Item> = ArrayList()

    private val mViewModel: DeviceViewerViewModel by lazy {
        ViewModelProviders.of(this, InjectorUtils.provideDeviceViewerViewModelFactory())
            .get(DeviceViewerViewModel::class.java)
    }


    private val dataChangedListener =
        Observer<ArrayList<String>> { it?.let { updateView(it[mViewModel.currentMachineID]) } }
    private val pushChangedListener =
        Observer<ArrayList<String>> { it?.let { pushUpdateView(it[mViewModel.currentMachineID]) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "OnCreate")
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        mViewModel.getData().observe(this, dataChangedListener)
        mViewModel.getPush().observe(this, pushChangedListener)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            machineID = it.getInt(ARG_MACHINE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "OnCreateView")
        val view = inflater.inflate(R.layout.captionlist_layout, container, false)
        setHasOptionsMenu(true)

        // Set the adapter
        if (view.data_list is RecyclerView) {
            with(view.data_list) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MainItemAdapter(items, listener)
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar wi7260ll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(InjectorUtils.EXTRA_KEY_MACHINE_ID, mViewModel.currentMachineID)
            startActivity(Intent(context, SettingsActivity::class.java))
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun pushUpdateView(data: String) {
        val datas = data.split(",");
        datas?.let {
            val time = datas[0]
            val content = datas[1]
            push_view_value.text = "$time  $content"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateView(data: String) {
        device_name.text = DataParser.updateView(data, items) ?: "Parsing error"
        data_list.adapter?.notifyDataSetChanged() ?: let { Log.e(TAG, "Error. adapter is null") }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DataParser.Item?)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_MACHINE_ID = "machine_id"

        @JvmStatic
        fun newInstance(machineID: Int, columnCount: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putInt(ARG_MACHINE_ID, machineID)
                }
            }
    }
}
