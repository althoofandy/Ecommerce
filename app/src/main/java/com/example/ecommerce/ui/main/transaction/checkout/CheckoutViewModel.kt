package com.example.ecommerce.ui.main.transaction.checkout

import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.Payment
import com.example.ecommerce.repos.EcommerceRepository

class CheckoutViewModel(private val repository: EcommerceRepository) : ViewModel() {

    fun doBuyProducts(token: String, payment: Payment) = repository.doBuyProducts(token, payment)
}
