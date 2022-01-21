package com.solluzfa.solluzviewer.view.list

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.solluzfa.solluzviewer.view.DataParser
import com.solluzfa.solluzviewer.view.detail.MainFragment
import com.solluzfa.solluzviewer.view.setting.SettingsActivity
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModel
import kotlinx.android.synthetic.main.fragment_machine_list.*
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 */
class MachineListFragment : Fragment() {

    private var columnCount = 1

    private val mViewModel: DeviceViewerViewModel by lazy {
        ViewModelProviders.of(this, InjectorUtils.provideDeviceViewerViewModelFactory())
            .get(DeviceViewerViewModel::class.java)
    }

    private val dataChangedListener =
        Observer<ArrayList<String>> {
            Log.i(TAG, "dataChangedListener: ${it?.size}")
            it?.let {
                updateView(it)
            }
        }


    private val tempItems: MutableList<DataParser.Item> = ArrayList()
    private var tempTitle: String? = null

    @SuppressLint("NotifyDataSetChanged")
    private fun updateView(eachMachineDataList: ArrayList<String>) {
        MachineListContent.ITEMS.clear()
        for(i in 0 until eachMachineDataList.size) {
            tempTitle = DataParser.parse(eachMachineDataList[i], tempItems)
            MachineListContent.ITEMS.add(i,
                MachineListContent.PlaceholderItem(
                    tempTitle,
                    tempItems[0].tt, tempItems[0].tb, tempItems[0].tf, tempItems[0].ta
                )
            )
        }
        machine_list.adapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        mViewModel.getData().observe(this, dataChangedListener)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_machine_list, container, false)
        setHasOptionsMenu(true)

        val recyclerView = view.findViewById<RecyclerView>(R.id.machine_list)

        // Set the adapter
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MachineListAdapter(MachineListContent.ITEMS, this@MachineListFragment)
                addItemDecoration(RecyclerHeightDecoration(3))
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_add_machine) {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(InjectorUtils.EXTRA_KEY_MACHINE_ID, mViewModel.getData().value?.size)
            startActivity(intent)
            true
        } else super.onOptionsItemSelected(item)
    }

    fun transact(machineID: Int) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        transaction.replace(R.id.content_main, MainFragment.newInstance(machineID, 1))
        transaction.commit()
    }

    companion object {
        private const val TAG = "MachineListFragment"

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            MachineListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}