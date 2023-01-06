package com.skripsi.optik_kasih.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressPref(
    val id: String,
    val label: String,
    val address: String,
    val kecamatan: String,
    val kelurahan: String,
    val postal: String,
) : Parcelable
