package com.robbyyehezkiel.robustaroasting.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.robbyyehezkiel.robustaroasting.data.model.Roast
import com.robbyyehezkiel.robustaroasting.databinding.ItemListRoastBinding

class RoastListAdapter(private val onItemClickCallback: (Roast, Int) -> Unit) :
    ListAdapter<Roast, RoastListAdapter.RoastViewHolder>(RoastComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoastViewHolder {
        val binding =
            ItemListRoastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoastViewHolder(private val binding: ItemListRoastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val roast = getItem(position)
                    onItemClickCallback(roast, position)
                }
            }
        }

        fun bind(roast: Roast) {
            binding.edListRoastTitle.text = roast.title
            binding.edListRoastTemperature.text = roast.temperature
            binding.edListRoastPhoto.setImageResource(roast.photoResId)
        }
    }

    class RoastComparator : DiffUtil.ItemCallback<Roast>() {
        override fun areItemsTheSame(oldItem: Roast, newItem: Roast): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Roast, newItem: Roast): Boolean {
            return oldItem.title == newItem.title
        }
    }
}
