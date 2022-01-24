package com.solluzfa.solluzviewer.view.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.solluzfa.solluzviewer.databinding.FragmentMachineListItemBinding
import com.solluzfa.solluzviewer.view.list.MachineListContent.PlaceholderItem


class MachineListAdapter(
    private val values: List<PlaceholderItem>,
    private val fragment: MachineListFragment
) : RecyclerView.Adapter<MachineListAdapter.ViewHolder>() {

    var showCheckBox: Boolean = false

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

        with(holder.stateTextView) {
            setBackgroundColor(item.tb)
            setTextColor(item.tf)
            text = item.tt
            gravity = item.ta
            setOnClickListener {
                fragment.transact(position)
            }
        }

        with(holder.finalTextView) {
            setBackgroundColor(item.ftb)
            setTextColor(item.ftf)
            text = item.ftt
            gravity = item.ta
            setOnClickListener {
                fragment.transact(position)
            }
        }

        holder.checkBoxView.tag = position

        if (showCheckBox) {
            with(holder.checkBoxView) {
                isChecked = item.deleteChecked
                visibility = View.VISIBLE
                setOnCheckedChangeListener { checkbox, isChecked ->
                    values[checkbox.tag as Int].deleteChecked = isChecked
                    fragment.updateDeleteAllState()
                }
            }
        } else {
            holder.checkBoxView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentMachineListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBoxView: CheckBox = binding.deleteCheckBox
        val titleView: TextView = binding.titleTextView
        val stateTextView: TextView = binding.stateTextView
        val finalTextView: TextView = binding.finalTextView

        override fun toString(): String {
            return super.toString() + " '" + stateTextView.text + "'"
        }
    }

}