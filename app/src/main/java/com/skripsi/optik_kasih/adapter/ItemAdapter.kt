package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.CartItemBinding
import com.skripsi.optik_kasih.databinding.ItemListBinding
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.Cart

class ItemAdapter() :
    ListAdapter<Cart, ItemAdapter.CartViewHolder>(DiffCallback()) {

    private lateinit var context: Context

    inner class CartViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cart) {
            binding.apply {
                itemName.text = item.name
                totalPrice.text = Utilities.convertPrice(((item.basePrice - (item.discount ?: 0.0)) * item.quantity).toString())
                price.bfrDiscPrice.apply {
                    isVisible = item.discount != null && item.discount > 0
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = Utilities.convertPrice((item.basePrice).toString())
                }
                price.price.text = Utilities.convertPrice((item.basePrice - (item.discount ?: 0.0)).toString())
                quantity.text = context.getString(R.string.item_quantity, item.quantity)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        context = parent.context
        return CartViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}