package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.databinding.AddressListItemBinding
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.utils.Utilities.addressDetail

class AddressAdapter(private val buttonCallback: (Address) -> Unit) :
    ListAdapter<Address, AddressAdapter.AddressViewHolder>(DiffCallback()) {
    private lateinit var context: Context

    inner class AddressViewHolder(private val binding: AddressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Address) {
            binding.apply {
                addressName.text = item.label
                addressUserName.text = item.customer.customer.customer_name
                addressPhoneNumber.text = item.customer.customer.phone_number
                address.text = item.addressDetail
                root.setOnClickListener {
                    buttonCallback.invoke(item)
                }
                btnOption.setOnClickListener {
                    buttonCallback.invoke(item)
                }
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