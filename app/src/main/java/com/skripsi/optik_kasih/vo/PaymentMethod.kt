package com.skripsi.optik_kasih.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethod(
    val id: Int,
    val image: Int,
    val group: String,
    val name: String,
): Parcelable

