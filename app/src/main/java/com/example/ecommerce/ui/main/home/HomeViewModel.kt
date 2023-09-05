package com.example.ecommerce.ui.main.home

import androidx.lifecycle.ViewModel
import com.example.ecommerce.ui.main.db.ProductDatabase

class HomeViewModel(private val productDatabase: ProductDatabase):ViewModel() {
     fun clearDb(){
        productDatabase.clearAllTables()
    }
}