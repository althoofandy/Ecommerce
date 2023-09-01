package com.example.ecommerce.ui.main.menu.cart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.ProductLocalDb
import com.example.ecommerce.ui.main.menu.db.ProductDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class CartViewModel(context: Context) : ViewModel() {

    private var cartDb: ProductDatabase? = ProductDatabase.getDatabase(context)
    private var cartDAO = cartDb?.productDao()

    fun addToCart(
        productId: String,
        productName: String,
        productPrice: Int,
        image: String,
        stock: Int?,
        variantName: String,
        variantPrice: Int?,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val productItem = ProductLocalDb(
                productId,
                productName,
                productPrice,
                image,
                "",
                "",
                "",
                0,
                stock,
                0,
                0,
                0,
                0F,
                variantName,
                variantPrice
            )
            cartDAO?.addToCart(productItem)
        }
    }

    fun getCartItem(): LiveData<List<ProductLocalDb>>? {
        return cartDAO?.getCartProducts()
    }

    fun removeFromCart(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            cartDAO?.removeFromCart(id)
        }
    }

    fun removeFromCartAll(productId: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            cartDAO?.removeFromCartAll(productId)
        }
    }

    fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            cartDAO?.updateCartItemQuantity(productId, newQuantity)
        }
    }

    fun updateCartItemCheckbox(productId: List<String>, isSelected: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            cartDAO?.updateCartItemCheckbox(productId, isSelected)
        }
    }

}

