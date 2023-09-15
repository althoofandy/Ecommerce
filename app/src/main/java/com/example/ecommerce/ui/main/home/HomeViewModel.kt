package com.example.ecommerce.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.ui.main.db.ProductDatabase

class HomeViewModel(private val productDatabase: ProductDatabase,private val sharedPref: SharedPref):ViewModel() {
     fun clearDb(){
        productDatabase.clearAllTables()
    }
}