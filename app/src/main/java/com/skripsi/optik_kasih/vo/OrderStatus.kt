package com.skripsi.optik_kasih.vo

import androidx.annotation.StringRes
import com.skripsi.optik_kasih.R

enum class OrderStatus(@StringRes val label: Int, val value: Int) {
    WaitingForPayment(R.string.waiting_for_payment, 1),
    Processed(R.string.processed, 2),
    Sent(R.string.sent, 3),
    Received(R.string.received, 4),
    Canceled(R.string.cancel, 5);

    companion object {
        private val map = values().associateBy(OrderStatus::value)
        fun fromValue(value: Int?): OrderStatus? =
            map[value]
    }
}
