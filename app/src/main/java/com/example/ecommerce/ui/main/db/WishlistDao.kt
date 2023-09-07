package com.example.ecommerce.ui.main.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecommerce.model.WishlistProduct

@Dao
interface WishlistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToWishlist(wishlist: WishlistProduct)

    @Query("DELETE FROM WishlistProduct WHERE WishlistProduct.productId = :id")
    suspend fun removeWishlist(id: String): Int

    @Query("SELECT * FROM WishlistProduct")
    fun getWishlistProducts(): LiveData<List<WishlistProduct>>

    @Query("SELECT * FROM WishlistProduct WHERE productId = :productId")
    fun getProductWishlistById(productId: String): WishlistProduct?




}