package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.databinding.PaymentMethodRowHeaderBinding
import com.skripsi.optik_kasih.databinding.PaymentMethodRowListBinding
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.vo.PaymentMethod

class PaymentMethodAdapter(private val buttonCallback: (PaymentMethod) -> Unit): ListAdapter<PaymentMethodRow, RecyclerView.ViewHolder>(DiffCallback()) {
    private lateinit var context: Context
    var selectedPaymentMethod: PaymentMethod? = null
    fun selectedPaymentMethodSetter(paymentMethod: PaymentMethod?) {
        if (selectedPaymentMethod == paymentMethod) {
            selectedPaymentMethod = null
        } else {
            val oldSelectedPaymentMethod: PaymentMethod? = selectedPaymentMethod
            selectedPaymentMethod = paymentMethod
            oldSelectedPaymentMethod?.let {
                notifyItemChanged(currentList.indexOf(currentList.find { it -> it.rowType == PaymentMethodAdapterType.List && == oldSelectedPaymentMethod..id }))
            }
        }
        notifyItemChanged(currentList.indexOf(address))
    }
    inner class HeaderViewHolder(private val binding: PaymentMethodRowHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentMethodRow.Header) {
            binding.apply {
                root.text = item.title
            }
        }
    }

    inner class ListViewHolder(private val binding: PaymentMethodRowListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentMethodRow.List) {
            binding.apply {
                val (_, image, group, name) = item.paymentMethod
                itemImg.setImageResource(image)
                this.name.text = name
                type.text = group
                root.setOnClickListener {
                    buttonCallback.invoke(item.paymentMethod)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PaymentMethodRow>() {
        override fun areItemsTheSame(oldItem: PaymentMethodRow, newItem: PaymentMethodRow) : Boolean {
            return when {
                oldItem is PaymentMethodRow.List && newItem is PaymentMethodRow.List -> {
                    oldItem == newItem
                }
                oldItem is PaymentMethodRow.Header && newItem is PaymentMethodRow.Header -> {
                    oldItem == newItem
                }
                else -> false
            }
        }


        override fun areContentsTheSame(oldItem: PaymentMethodRow, newItem: PaymentMethodRow) : Boolean {
            return when {
                oldItem is PaymentMethodRow.List && newItem is PaymentMethodRow.List -> {
                    oldItem == newItem
                }
                oldItem is PaymentMethodRow.Header && newItem is PaymentMethodRow.Header -> {
                    oldItem == newItem
                }
                else -> false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (PaymentMethodAdapterType.values()[viewType]) {
            PaymentMethodAdapterType.List -> ListViewHolder(
                PaymentMethodRowListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            PaymentMethodAdapterType.Header -> HeaderViewHolder(
                PaymentMethodRowHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val paymentMethodRow = getItem(position)) {
            is PaymentMethodRow.List -> (holder as ListViewHolder).bind(paymentMethodRow)
            is PaymentMethodRow.Header -> (holder as HeaderViewHolder).bind(paymentMethodRow)
        }
    }

    override fun getItemViewType(position: Int): Int =
        getItem(position).rowType.ordinal
}

sealed class PaymentMethodRow(val rowType: PaymentMethodAdapterType) {
    data class List(val paymentMethod: PaymentMethod) : PaymentMethodRow(PaymentMethodAdapterType.List)
    data class Header(val title: String) : PaymentMethodRow(PaymentMethodAdapterType.Header)
}

enum class PaymentMethodAdapterType {
    List,
    Header
}