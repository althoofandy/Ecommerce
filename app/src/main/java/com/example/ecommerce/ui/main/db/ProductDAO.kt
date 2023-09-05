package com.example.ecommerce.ui.main.db

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

    @Query("SELECT * FROM ProductLocalDb")
    fun getCartProducts(): LiveData<List<ProductLocalDb>>

    @Query("SELECT * FROM ProductLocalDb WHERE productId = :productId")
    fun getProductById(productId: String): ProductLocalDb?

    @Query("UPDATE ProductLocalDb SET quantity = :newQuantity WHERE productId = :productId")
    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int)

    @Query("UPDATE ProductLocalDb SET selected = :isSelected WHERE productId IN (:productId)")
    suspend fun updateCartItemCheckbox(productId: List<String>, isSelected: Boolean)

    @Query("DELETE FROM ProductLocalDb WHERE ProductLocalDb.productId = :id")
    suspend fun removeFromCart(id: String): Int

    @Query("DELETE FROM ProductLocalDb WHERE ProductLocalDb.productId IN (:productId)")
    suspend fun removeFromCartAll(productId: List<String>)
}
