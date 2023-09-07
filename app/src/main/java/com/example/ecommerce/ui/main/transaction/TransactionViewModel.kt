package com.example.ecommerce.ui.main.transaction

import androidx.lifecycle.ViewModel
import com.example.ecommerce.repos.EcommerceRepository

class TransactionViewModel(private val repository: EcommerceRepository):ViewModel() {

    fun getTransactionHistory(token:String) = repository.getTransactionHistory(token)

}