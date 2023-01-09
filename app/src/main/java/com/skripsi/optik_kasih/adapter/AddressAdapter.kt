package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.AddressListItemBinding
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.utils.Utilities.addressDetail
import com.skripsi.optik_kasih.utils.Utilities.dpToPx

class AddressAdapter:
    ListAdapter<Address, AddressAdapter.AddressViewHolder>(DiffCallback()) {
    private var primaryAddress: Address? = null
    private var rootCallback: ((Address) -> Unit)? = null
    fun setRootCallback(callback: (Address) -> Unit) {
        rootCallback = callback
    }
    private var optionCallback: ((Address) -> Unit)? = null
    fun setOptionCallback(callback: (Address) -> Unit) {
        optionCallback = callback
    }
    var selectedAddress: Address? = null
    fun setPrimaryAddress(address: Address?) {
        val oldPrimaryAddress: Address? = primaryAddress
        primaryAddress = address
        oldPrimaryAddress?.let {
            notifyItemChanged(currentList.indexOf(oldPrimaryAddress))
        }
        notifyItemChanged(currentList.indexOf(address))
    }

    fun selectedAddressSetter(address: Address?) {
        if (selectedAddress == address) {
            selectedAddress = null
        } else {
            val oldSelectedAddress: Address? = selectedAddress
            selectedAddress = address
            oldSelectedAddress?.let {
                notifyItemChanged(currentList.indexOf(oldSelectedAddress))
            }
        }
        notifyItemChanged(currentList.indexOf(address))
    }

    private lateinit var context: Context

    inner class AddressViewHolder(private val binding: AddressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Address) {
            val isPrimary = item == primaryAddress
            binding.apply {
                root.isChecked = selectedAddress != null && item == selectedAddress
                addressName.text = item.label
                addressUserName.text = item.customer.customer.customer_name
                addressPhoneNumber.text = item.customer.customer.phone_number
                address.text = item.addressDetail
                root.setOnClickListener {
                    rootCallback?.invoke(item)
                }
                btnOption.setOnClickListener {
                    optionCallback?.invoke(item)
                }
                addressName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (isPrimary) R.color.primaryColor else android.R.color.tab_indicator_text
                    )
                )
                primaryStatus.isVisible = isPrimary
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        context = parent.context
        return AddressViewHolder(
            AddressListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}