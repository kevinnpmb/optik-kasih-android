package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.HistoryItemListBinding
import com.skripsi.optik_kasih.fragment.Order
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.OrderStatus

class HistoryAdapter(private val buttonCallback: (Order) -> Unit) :
    ListAdapter<Order, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    private lateinit var context: Context

    inner class HistoryViewHolder(private val binding: HistoryItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Order) {
            binding.apply {
                orderId.text = context.getString(
                    R.string.order_id_template,
                    Utilities.zeroPadding(item.id.toInt())
                )
                date.text = Utilities.formatISO8601ToDateString(
                    item.order_date.toString(),
                    Utilities.DateType.VIEW
                )
                status.text = OrderStatus.fromValue(item.order_status)?.label?.let {
                    context.getString(it)
                } ?: "-"
                root.setOnClickListener {
                    buttonCallback.invoke(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        context = parent.context
        return HistoryViewHolder(
            HistoryItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    enum class Type {
        INCREMENT, DECREMENT
    }
}