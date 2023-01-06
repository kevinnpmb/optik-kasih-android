package com.skripsi.optik_kasih.repository

import androidx.lifecycle.MutableLiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apollographql.apollo3.ApolloClient
import com.skripsi.optik_kasih.GetProductQuery
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
//import com.skripsi.optik_kasih.LoginQuery
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.database.dao.CartDao
import com.skripsi.optik_kasih.type.LoginParam
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.vo.Cart
import com.skripsi.optik_kasih.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    var cartDao: CartDao
) {
    suspend fun insert(cartData: Cart) = cartDao.insert(cartData)

    suspend fun insert(cartListData: List<Cart>) = cartDao.insert(cartListData)

    suspend fun deleteAll() = cartDao.deleteAll()

    suspend fun deleteCart(id: String) = cartDao.deleteCart(id)

    suspend fun getCart(id: String) = cartDao.getCart(id)

    suspend fun getCart() = cartDao.getCart()

    suspend fun update(cartData: Cart) = cartDao.update(cartData)

    suspend fun isCartDuplicate(id: String) = cartDao.isCartDuplicate(id)
}