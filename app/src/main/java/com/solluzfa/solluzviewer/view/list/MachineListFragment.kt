package com.solluzfa.solluzviewer.view.list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import com.solluzfa.solluzviewer.Log
import com.solluzfa.solluzviewer.R
import com.solluzfa.solluzviewer.utils.InjectorUtils
import com.solluzfa.solluzviewer.view.DataParser
import com.solluzfa.solluzviewer.view.IOnBackPressed
import com.solluzfa.solluzviewer.view.detail.MainFragment
import com.solluzfa.solluzviewer.view.setting.SettingsActivity
import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_machine_list.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

/**
 * A fragment representing a list of Items.
 */
class MachineListFragment : Fragment(), IOnBackPressed {

    private var columnCount = 1
    var dialog: Dialog? = null

    private val mViewModel: DeviceViewerViewModel by lazy {
        ViewModelProviders.of(this,
            activity?.let { InjectorUtils.provideDeviceViewerViewModelFactory(it) })
            .get(DeviceViewerViewModel::class.java)
    }

    private val dataChangedListener =
        Observer<ArrayList<String>> {
            Log.i(TAG, "dataChangedListener: machine count: ${it?.size}")
            it?.let {
                updateView(it)
            }
        }


    private val tempItems: MutableList<DataParser.Item> = ArrayList()
    private var tempTitle: String? = null

    @SuppressLint("NotifyDataSetChanged")
    private fun updateView(eachMachineDataList: ArrayList<String>) {
        Log.i(TAG, "MachineListContent.ITEMS.size ${MachineListContent.ITEMS.size}" +
                ", eachMachineDataList.size ${eachMachineDataList.size}")

        if (eachMachineDataList.size != MachineListContent.ITEMS.size) {
            MachineListContent.ITEMS.clear()
            for (i in 0 until eachMachineDataList.size) {
                tempTitle = DataParser.parse(eachMachineDataList[i], tempItems)
                if (tempItems.size > FINAL_INDEX) {
                    MachineListContent.ITEMS.add(
                        MachineListContent.PlaceholderItem(
                            tempTitle,
                            tempItems[TITLE_INDEX].tt, tempItems[TITLE_INDEX].tb, tempItems[TITLE_INDEX].tf, tempItems[TITLE_INDEX].ta,
                            tempItems[FINAL_INDEX].tt, tempItems[FINAL_INDEX].tb, tempItems[FINAL_INDEX].tf, tempItems[FINAL_INDEX].ta
                        )
                    )
                } else if (tempItems.size > TITLE_INDEX) {
                    MachineListContent.ITEMS.add(
                        MachineListContent.PlaceholderItem(
                            tempTitle,
                            tempItems[TITLE_INDEX].tt, tempItems[TITLE_INDEX].tb, tempItems[TITLE_INDEX].tf, tempItems[TITLE_INDEX].ta
                        )
                    )
                }
            }
        } else {
            for (i in 0 until eachMachineDataList.size) {
                tempTitle = DataParser.parse(eachMachineDataList[i], tempItems)
                with(MachineListContent.ITEMS[i]) {
                    title = tempTitle
                    if (tempItems.size > TITLE_INDEX) {
                        tt = tempItems[TITLE_INDEX].tt
                        tb = tempItems[TITLE_INDEX].tb
                        tf = tempItems[TITLE_INDEX].tf
                        ta = tempItems[TITLE_INDEX].ta
                    }

                    if (tempItems.size > FINAL_INDEX) {
                        ftt = tempItems[FINAL_INDEX].tt
                        ftb = tempItems[FINAL_INDEX].tb
                        ftf = tempItems[FINAL_INDEX].tf
                        fta = tempItems[FINAL_INDEX].ta
                    }
                }
            }
        }
        machineListView.adapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        mViewModel.getData().observe(this, dataChangedListener)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_machine_list, container, false)
        setHasOptionsMenu(true)

        val recyclerView = view.findViewById<RecyclerView>(R.id.machineListView)
        val selectAllCheckBoxView = view.findViewById<CheckBox>(R.id.selectAllCheckBox)
        val deleteButtonView = view.findViewById<Button>(R.id.deleteButton)

        // Set the adapter
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MachineListAdapter(MachineListContent.ITEMS, this@MachineListFragment)
                addItemDecoration(RecyclerHeightDecoration(15))
            }
        }

        selectAllCheckBoxView.setOnClickListener { checkBox ->
            MachineListContent.ITEMS.forEach {
                it.deleteChecked = (checkBox as CheckBox).isChecked
            }
            machineListView.adapter?.notifyDataSetChanged()
        }

        deleteButtonView.setOnClickListener {
            val intArray = ArrayList<Int>()
            MachineListContent.ITEMS.forEachIndexed { index, placeholderItem ->
                if (placeholderItem.deleteChecked) {
                    Log.i(TAG, "Delete Item machineID: $index")
                    intArray.add(index)
                }
            }
            showProgressBarShortly()
            mViewModel.removeMachines(intArray)
            toggleDeleteMachineView(false)
        }

        return view
    }

    @SuppressLint("CheckResult")
    private fun showProgressBarShortly() {
        dialog?.show()
        Timer().schedule(1000) {
            dialog?.dismiss()
        }
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setView(R.layout.progress)
        dialog = builder.create()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        inflater?.inflate(R.menu.list_menu, menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_add_machine) {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(InjectorUtils.EXTRA_KEY_MACHINE_ID, mViewModel.getData().value?.size)
            startActivity(intent)
            true
        } else if (id == R.id.action_remove_machine) {
            toggleDeleteMachineView(true)
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

        const val TITLE_INDEX = 0
        const val FINAL_INDEX = 4

        @JvmStatic
        fun newInstance(columnCount: Int) =
            MachineListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onBackPressed(): Boolean {
        with((machineListView?.adapter as MachineListAdapter)) {
            if (showCheckBox) {
                toggleDeleteMachineView(false)
                return true
            }
        }
        return false
    }

    @SuppressLint("NotifyDataSetChanged", "NewApi")
    private fun toggleDeleteMachineView(show: Boolean) {
        showDeleteButton(show)
        selectAllCheckBox.visibility = if (show) View.VISIBLE else View.GONE
        with((machineListView?.adapter as MachineListAdapter)) {
            showCheckBox = show
            notifyDataSetChanged()
            if (!show) MachineListContent.ITEMS.forEach {
                it.deleteChecked = false
                selectAllCheckBox.isChecked = false
            }
        }
    }

    @SuppressLint("NewApi")
    private fun showDeleteButton(show: Boolean) {
        val slideTransition = Slide(Gravity.BOTTOM)
        slideTransition.duration = 200
        slideTransition.addTarget(deleteButton)
        TransitionManager.beginDelayedTransition(machineListMainView, slideTransition)
        deleteButton.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun updateDeleteAllState() {
        MachineListContent.ITEMS.all {
            it.deleteChecked
        }.let { result -> selectAllCheckBox.isChecked = result }
    }
}