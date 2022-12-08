package com.example.optik_kasih.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.optik_kasih.databinding.HomeRowHeaderBinding
import com.example.optik_kasih.databinding.HomeRowListBinding

class HomeAdapter(private val buttonCallback: () -> Void): ListAdapter<HomeRow, RecyclerView.ViewHolder>(DiffCallback()) {
    private lateinit var context: Context
    inner class HeaderViewHolder(private val binding: HomeRowHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeRow.Header) {
            binding.apply {
                root.text = item.title
            }
        }
    }

    inner class ListViewHolder(private val binding: HomeRowListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeRow.List) {
            binding.apply {

            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HomeRow>() {
        override fun areItemsTheSame(oldItem: HomeRow, newItem: HomeRow) : Boolean {
            return when {
                oldItem is HomeRow.List && newItem is HomeRow.List -> {
                    oldItem == newItem
                }
                oldItem is HomeRow.Header && newItem is HomeRow.Header -> {
                    oldItem == newItem
                }
                else -> false
            }
        }


        override fun areContentsTheSame(oldItem: HomeRow, newItem: HomeRow) : Boolean {
            return when {
                oldItem is HomeRow.List && newItem is HomeRow.List -> {
                    oldItem == newItem
                }
                oldItem is HomeRow.Header && newItem is HomeRow.Header -> {
                    oldItem == newItem
                }
                else -> false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (HomeAdapterType.values()[viewType]) {
            HomeAdapterType.List -> ListViewHolder(
                HomeRowListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            HomeAdapterType.Header -> HeaderViewHolder(
                HomeRowHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val homeRow = getItem(position)) {
            is HomeRow.List -> (holder as ItemViewHolder).bind(tableRow)
            is HomeRow.Header -> (holder as HeaderViewHolder).bind(tableRow)
        }
    }
}

sealed class HomeRow(val rowType: HomeAdapterType) {
    data class List(val string: String) :
        HomeRow(HomeAdapterType.Header)

    data class Header(val title: String) : HomeRow(HomeAdapterType.List)
}

enum class HomeAdapterType {
    Header,
    List
}