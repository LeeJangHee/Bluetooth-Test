package com.clonecode.bluetoothtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clonecode.bluetoothtest.databinding.ItemBluetoothBinding

class BTAdapter: ListAdapter<BTModel, BTAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemBluetoothBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(btModel: BTModel) {
            binding.apply {
                bt = btModel
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBluetoothBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BTModel>() {
            override fun areItemsTheSame(oldItem: BTModel, newItem: BTModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BTModel, newItem: BTModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}