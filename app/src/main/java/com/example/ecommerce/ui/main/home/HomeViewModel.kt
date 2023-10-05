package com.example.ecommerce.ui.main.home

import androidx.lifecycle.ViewModel
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.db.ProductDatabase

class HomeViewModel(
    private val productDatabase: ProductDatabase,
    private val sharedPref: SharedPref,
) : ViewModel() {
    fun clearDb() {
        productDatabase.clearAllTables()
    }
}
