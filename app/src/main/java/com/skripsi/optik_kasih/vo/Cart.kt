package com.skripsi.optik_kasih.vo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skripsi.optik_kasih.fragment.OrderProduct
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cart_table")
data class Cart(
    @PrimaryKey
    val id: String,
    val name: String? = null,
    val image: String? = null,
    val basePrice: Double,
    val discount: Double? = null,
    val description: String? = null,
    val quantity: Int = 0
) : Parcelable

fun List<Cart>.countDiscount() = sumOf { cart -> (cart.discount ?: 0.0) * cart.quantity }

fun List<Cart>.countSubtotal() = sumOf { cart -> cart.basePrice * cart.quantity }

fun List<Cart>.countTotalQty() = sumOf { cart -> cart.quantity }

fun List<Cart>.countTotal() = countSubtotal() - countDiscount()

fun OrderProduct.toCart(): Cart {
    val (
        id,
        product_name,
        product_type,
        product_brand,
        product_description,
        product_price,
        product_image,
        discount,
        product_stock,
        supplier_id,
        isShown,
    ) = product?.product!!
    return Cart(
        product_id, product_name, product_image, product_price, discount, product_description, qty
    )
}