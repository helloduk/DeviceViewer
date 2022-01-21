package com.solluzfa.solluzviewer.view.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.solluzfa.solluzviewer.databinding.FragmentMachineListItemBinding
import com.solluzfa.solluzviewer.view.list.MachineListContent.PlaceholderItem


class MachineListAdapter(
    private val values: List<PlaceholderItem>,
    private val fragment: MachineListFragment
) : RecyclerView.Adapter<MachineListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentMachineListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        with(holder.titleView) {
            text = item.title
            setOnClickListener {
                fragment.transact(position)
            }
        }

        with(holder.valueView) {
            setBackgroundColor(item.tb)
            setTextColor(item.tf)
            text = item.tt
            gravity = item.ta
            setOnClickListener {
                fragment.transact(position)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentMachineListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.titleTextView
        val valueView: TextView = binding.valueTextView

        override fun toString(): String {
            return super.toString() + " '" + valueView.text + "'"
        }
    }

}