package com.skripsi.optik_kasih.utils

import com.skripsi.optik_kasih.vo.User

object Utilities {
    fun com.skripsi.optik_kasih.fragment.User.toUser(accessToken: String) = User(
        id,
        customer_name,
        customer_gender,
        customer_birthday.toString(),
        phone_number,
        customer_email,
        accessToken,
    )
}