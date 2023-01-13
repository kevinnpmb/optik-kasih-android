package com.skripsi.optik_kasih.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityInfo(
    val ward: String,
    val district: String,
    val regency: String,
    val province: String,
    val postcode: String,
): Parcelable
