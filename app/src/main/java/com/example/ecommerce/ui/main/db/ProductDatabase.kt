package com.example.ecommerce.ui.main.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ecommerce.model.ProductLocalDb
import com.example.ecommerce.model.WishlistProduct

@Database(entities = [ProductLocalDb::class, WishlistProduct::class], version = 1, exportSchema = true)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDAO
    abstract fun wishListDao(): WishlistDao
    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null
        fun getDatabase(context: Context): ProductDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "db_product"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}