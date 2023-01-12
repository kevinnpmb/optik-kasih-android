package com.skripsi.optik_kasih.vo

import androidx.annotation.StringRes
import com.skripsi.optik_kasih.R

enum class PaymentStatus(@StringRes val label: Int, val value: Int) {
    NotPaid(R.string.not_paid, 1),
    Paid(R.string.paid, 2);

    companion object {
        private val map = values().associateBy(PaymentStatus::value)
        fun fromValue(value: Int?): PaymentStatus? =
            map[value]
    }
}
