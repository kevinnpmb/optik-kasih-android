package com.skripsi.optik_kasih.database.dao

import androidx.room.*
import com.skripsi.optik_kasih.vo.Cart


@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartData: Cart)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartListData: List<Cart>)

    @Query("DELETE FROM cart_table")
    suspend fun deleteAll()

    @Query("DELETE FROM cart_table WHERE id = :id")
    suspend fun deleteCart(id: String)

    @Query("SELECT * FROM cart_table WHERE id = :id")
    suspend fun getCart(id: String): Cart?

    @Query("SELECT * FROM cart_table")
    suspend fun getCart(): List<Cart>

    @Query("SELECT COUNT(*) FROM cart_table")
    suspend fun getCartCount(): Int

    @Update
    suspend fun update(cartData: Cart)

    @Query("SELECT EXISTS(SELECT 1 FROM cart_table WHERE id = :id)")
    suspend fun isCartDuplicate(id: String): Boolean

}