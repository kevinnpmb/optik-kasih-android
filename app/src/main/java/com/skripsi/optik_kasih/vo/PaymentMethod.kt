package com.skripsi.optik_kasih.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethod(
    val id: String,
    val image: String?,
    val group: String,
    val name: String?,
    val virtualAccount: String?
): Parcelable

