package com.skripsi.optik_kasih.vo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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