package com.example.ecommerce.ui.main.menu.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecommerce.model.ProductLocalDb

@Dao
interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToCart(cartProducts: ProductLocalDb)

    @Query("SELECT * FROM product_database")
    fun getCartProducts(): LiveData<List<ProductLocalDb>>

    @Query("UPDATE product_database SET quantity = :newQuantity WHERE productId = :productId")
    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int)

    @Query("UPDATE product_database SET selected = :isSelected WHERE productId IN (:productId)")
    suspend fun updateCartItemCheckbox(productId: List<String>, isSelected: Boolean)

    @Query("DELETE FROM product_database WHERE product_database.productId = :id")
    suspend fun removeFromCart(id: String): Int

    @Query("DELETE FROM product_database WHERE product_database.productId IN (:productId)")
    suspend fun removeFromCartAll(productId: List<String>)
}
