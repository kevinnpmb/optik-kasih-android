package com.skripsi.optik_kasih.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skripsi.optik_kasih.database.dao.CartDao
import com.skripsi.optik_kasih.vo.Cart

@Database(entities = [Cart::class], version = 1)
abstract class OptikKasihDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}