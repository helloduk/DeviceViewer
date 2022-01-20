package com.solluzfa.solluzviewer.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solluzfa.solluzviewer.R

import com.solluzfa.solluzviewer.view.MainFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MainItemAdapter(
        private val mValues: List<DataParser.Item>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MainItemAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        val item = v.tag as DataParser.Item
        // Notify the active callbacks interface (the activity, if the fragment is attached to
        // one) that an item has been selected.
        mListener?.onListFragmentInteraction(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        //For Caption
        holder.mLayout.setBackgroundColor(item.cb)
        with(holder.mTitle) {
            setTextColor(item.cf)
            text = item.ct
        }

        //For Value
        with(holder.mValue) {
            setBackgroundColor(item.tb)
            setTextColor(item.tf)
            text = item.tt
            gravity = item.ta
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mLayout = mView.content_layout
        val mTitle = mView.title
        val mValue = mView.value
        override fun toString(): String {
            return super.toString() + " '" + mView.title + "'"
        }
    }
}
