package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.databinding.HomeRowHeaderBinding
import com.skripsi.optik_kasih.databinding.HomeRowListBinding
import com.skripsi.optik_kasih.fragment.Product
import com.skripsi.optik_kasih.utils.Utilities

class HomeAdapter(private val buttonCallback: (Product) -> Unit): ListAdapter<HomeRow, RecyclerView.ViewHolder>(DiffCallback()) {
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
                val (_, product_name, _, _, _, product_price) = item.product
                if (price.bfrDiscPrice.isVisible) {
                    price.bfrDiscPrice.apply {
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        text = Utilities.convertPrice(product_price.toString())
                    }
                }
                price.price.text = Utilities.convertPrice(product_price.toString())
                title.text = product_name
                root.setOnClickListener {
                    buttonCallback.invoke(item.product)
                }
                btnAdd.setOnClickListener {
                    buttonCallback.invoke(item.product)
                }
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
            is HomeRow.List -> (holder as ListViewHolder).bind(homeRow)
            is HomeRow.Header -> (holder as HeaderViewHolder).bind(homeRow)
        }
    }

    override fun getItemViewType(position: Int): Int =
        getItem(position).rowType.ordinal
}

sealed class HomeRow(val rowType: HomeAdapterType) {
    data class List(val product: Product) : HomeRow(HomeAdapterType.List)
    data class Header(val title: String) : HomeRow(HomeAdapterType.Header)
}

enum class HomeAdapterType {
    List,
    Header
}