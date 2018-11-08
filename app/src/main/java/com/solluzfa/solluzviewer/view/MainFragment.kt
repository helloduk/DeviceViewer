package com.solluzfa.solluzviewer.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solluzfa.solluzviewer.R

import com.solluzfa.solluzviewer.viewmodel.DeviceViewerViewModel
import kotlinx.android.synthetic.main.captionlist_layout.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [MainFragment.OnListFragmentInteractionListener] interface.
 */
class MainFragment : Fragment() {
    private val TAG = "MainFragment"
    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    private val mViewModel: DeviceViewerViewModel by lazy {
        ViewModelProviders.of(this).get(DeviceViewerViewModel::class.java)
    }

    data class Item(var tt: String, var tb: Int, var tf: Int, var ta: Int, var ct: String, var cb: Int, var cf: Int)

    val items: MutableList<Item> = ArrayList()

    private val dataChangedListener = Observer<String> { it?.let { updateView(it) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "OnCreate")
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        mViewModel.dataNotifier.observe(this, dataChangedListener)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG, "OnCreateView")
        val view = inflater.inflate(R.layout.captionlist_layout, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(items, listener)
            }
        }
        return view
    }

    private fun updateView(data: String) {
        Log.i(TAG, "updateView $data")
        //Row:4,TT1:Text1,TB1:255255255,TF1:000000000,TT2:Text2,TB2:243175175,TF2:000000000,TT3:Text3,TB3:255255255,TF3:000000000,TT4:Text4,TB4:255255255,TF4:000000000
        //Name:Title Text,Row:4,CT1:Caption1,CB1:011097019,CF1:255255255,TA1:Right,CT2:Caption2,CB2:164020020,CF2:255255255,TA2:Left,CT3:Caption3,CB3:052118232,CF3:000000000,TA3:Center,CT4:Caption4,CB4:023108097,CF4:255255255,TA4:Center

        val datas = data.split(" ")

        val valueData = datas[0]?.split(",")
        val layoutData = datas[1]?.split(",")

        val rows = valueData?.get(0)?.substringAfterLast(":").toInt()

        if (valueData != null && layoutData != null) {
            device_name.text = layoutData[0].substringAfterLast(":")

            rows?.let {
                if (items.size != rows)
                    items.clear()

                for (i in 0 until rows) {
                    val dataItem: Item = Item(
                            tt = valueData[1 + (i * 3)].substringAfterLast(":"),
                            tb = parseColor(valueData[2 + (i * 3)].substringAfterLast(":")),
                            tf = parseColor(valueData[3 + (i * 3)].substringAfterLast(":")),
                            ct = layoutData[2 + (i * 4)].substringAfterLast(":"),
                            cb = parseColor(layoutData[3 + (i * 4)].substringAfterLast(":")),
                            cf = parseColor(layoutData[4 + (i * 4)].substringAfterLast(":")),
                            ta = when (layoutData[5 + (i * 4)].substringAfterLast(":")) {
                                "Right" -> 5
                                "Left" -> 3
                                "Center" -> 11
                                else -> 5
                            }
                    )
                    if (items.get(i) == null)
                        items.add(i, dataItem)
                    else
                        items.set(i, dataItem)
                }
            }
        }
        data_list
    }

    private fun parseColor(input: String): Int {
        val red = input.substring(0, 3)
        val green = input.substring(3, 6)
        val blue = input.substring(6)

        return Color.rgb(red.toInt(), green.toInt(), blue.toInt())
    }

    //    private fun update(packet: String) {
//        Log.i(TAG, "update : " + packet)
//        //N:Solluz 비전 검사 장비,S:Y,G:100,N:2
//        val datas = packet.split(",")
//        if(datas.size >= 4) {
//            try {
//                val passedValue = datas[2].substringAfterLast(":").toInt()
//                val failedValue = datas[3].substringAfterLast(":").toInt()
//                name.value = datas[0].substringAfterLast(":")
//                state.value = datas[1].substringAfterLast(":").equals("Y")
//                passed.value = passedValue
//                failed.value = failedValue
//                total.value = passedValue + failedValue
//                passPercentage.value = passedValue.toFloat() * 100 / (passedValue + failedValue)
//            } catch(e : Exception) {
//                Log.e(TAG, "Invalid type ${e.toString()}");
//            }
//        } else {
//            Log.e(TAG, "Invalid type");
//        }
//    }


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Item?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                MainFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
