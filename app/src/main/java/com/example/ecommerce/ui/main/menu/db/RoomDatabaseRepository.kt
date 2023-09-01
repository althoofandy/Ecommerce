package com.example.ecommerce.ui.main.menu.db

import androidx.lifecycle.LiveData
import com.example.ecommerce.model.ProductLocalDb

class RoomDatabaseRepository(private val mCartProduct: ProductDAO) {
    suspend fun addToCart(product: ProductLocalDb) {
        mCartProduct.addToCart(product)
    }

    fun getCartProducts(): LiveData<List<ProductLocalDb>> {
        return mCartProduct.getCartProducts()
    }

    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        mCartProduct.updateCartItemQuantity(productId, newQuantity)
    }

    suspend fun updateCartItemCheckbox(productIdList: List<String>, isSelected: Boolean) {
        mCartProduct.updateCartItemCheckbox(productIdList, isSelected)
    }

    suspend fun removeFromCart(productId: String): Int {
        return mCartProduct.removeFromCart(productId)
    }

    suspend fun removeFromCartAll(productIdList: List<String>) {
        mCartProduct.removeFromCartAll(productIdList)
    }

}