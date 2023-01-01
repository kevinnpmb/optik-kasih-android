package com.skripsi.optik_kasih.vo

data class User(
    var id: String,
    var name: String,
    var gender: Int,
    var birthday: String,
    var phone_number: String,
    var email: String?,
    var accessToken: String? = null
)