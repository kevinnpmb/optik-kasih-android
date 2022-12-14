package com.skripsi.optik_kasih.utils


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.vo.AddressPref
import com.skripsi.optik_kasih.vo.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * General Preferences Helper class, used for storing preference values using the Preference API
 */
@Singleton
class PreferencesHelper @Inject constructor(@ApplicationContext context: Context) {

    @Inject
    lateinit var gson: Gson

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)

    var user: User?
        get() = pref.getString(PREF_KEY_USER, null)?.let { gson.fromJson(it, User::class.java) }
        set(user) = pref.edit().putString(PREF_KEY_USER, gson.toJson(user)).apply()

    val isLogin: Boolean get() = user?.accessToken != null

    var primaryAddress: AddressPref?
        get() = pref.getString(PREF_KEY_PRIMARY_ADDRESS, null)?.let { gson.fromJson(it, AddressPref::class.java) }
        set(user) = pref.edit().putString(PREF_KEY_PRIMARY_ADDRESS, gson.toJson(user)).apply()

    fun saveAccount(userData: User) {
        pref.edit().putString(PREF_KEY_USER, gson.toJson(userData)).apply()
    }

    fun signOut() {
        pref.edit().putString(PREF_KEY_USER, null).apply()
        pref.edit().putString(PREF_KEY_PRIMARY_ADDRESS, null).apply()
    }

//    var adminId: String
//        get() = pref.getString(PREF_KEY_ADMIN_ID, "") ?: ""
//        set(adminId) = pref.edit().putString(PREF_KEY_ADMIN_ID, adminId).apply()
//
//    var outletId: Int
//        get() = pref.getInt(PREF_KEY_OUTLET_ID, -1)
//        set(outletId) = pref.edit().putInt(PREF_KEY_OUTLET_ID, outletId).apply()

    companion object {
        private const val PREF_BUFFER_PACKAGE_NAME = "com.skripsi.optik_kasih"
        private const val PREF_KEY_USER = "access_token"
        private const val PREF_KEY_PRIMARY_ADDRESS = "primary_address"
    }
}
