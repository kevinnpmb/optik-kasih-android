package com.skripsi.optik_kasih.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.optik_kasih.databinding.CartItemBinding
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.Cart

class CartAdapter(private val buttonCallback: (Type, Cart, qty: Int) -> Unit) :
    ListAdapter<Cart, CartAdapter.CartViewHolder>(DiffCallback()) {

    private lateinit var context: Context

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cart) {
            binding.apply {
                var quantity = item.quantity
                Utilities.loadImageUrl(item.image, ivItem)
                cartItemName.text = item.name
                qtyChanger.quantity.text = item.quantity.toString()
                price.price.text =
                    Utilities.convertPrice((item.basePrice - (item.discount ?: 0.0)).toString())
                price.bfrDiscPrice.isVisible = item.discount != null
                price.bfrDiscPrice.text = Utilities.convertPrice((item.basePrice).toString())
                qtyChanger.btnAdd.setOnClickListener {
                    buttonCallback.invoke(Type.INCREMENT, item, ++quantity)
                    qtyChanger.quantity.text = quantity.toString()
                }
                qtyChanger.btnRemove.setOnClickListener {
                    buttonCallback.invoke(Type.DECREMENT, item, --quantity)
                    qtyChanger.quantity.text = quantity.toString()
                }
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
            CartItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    enum class Type {
        INCREMENT, DECREMENT
    }
}