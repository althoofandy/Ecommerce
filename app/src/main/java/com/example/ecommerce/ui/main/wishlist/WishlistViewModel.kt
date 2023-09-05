package com.example.ecommerce.ui.main.wishlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.WishlistProduct
import com.example.ecommerce.ui.main.db.ProductDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistViewModel(context: Context) : ViewModel() {
    private var cartDb: ProductDatabase? = ProductDatabase.getDatabase(context)
    private var wishlistDao = cartDb?.wishListDao()

    fun addToCart(
        productId: String,
        productName: String,
        productPrice: Int,
        image: String,
        store: String,
        sale: Int,
        stock: Int?,
        rating: Int,
        productRating: Float,
        variantName: String,
        variantPrice: Int?,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val wishlistItem = WishlistProduct(
                productId,
                productName,
                productPrice,
                image,
                "",
                "",
                store,
                sale,
                stock,
                rating,
                0,
                0,
                productRating,
                variantName,
                variantPrice
            )
            wishlistDao?.addToWishlist(wishlistItem)
        }
    }

    fun removeWishlist(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            wishlistDao?.removeWishlist(id)
        }
    }

    fun getProductWishlistById(id: String): WishlistProduct? {
        return wishlistDao?.getProductWishlistById(id)
    }

    fun getWishlistProduct(): LiveData<List<WishlistProduct>>? {
        return wishlistDao?.getWishlistProducts()
    }
}
