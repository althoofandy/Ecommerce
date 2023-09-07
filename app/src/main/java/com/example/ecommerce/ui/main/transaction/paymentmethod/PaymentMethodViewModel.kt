package com.example.ecommerce.ui.main.transaction.paymentmethod

import androidx.lifecycle.ViewModel
import com.example.ecommerce.repos.EcommerceRepository

class PaymentMethodViewModel(private val repository: EcommerceRepository):ViewModel() {

    fun getPaymentMethod(token:String) = repository.getPaymentMethods(token)
}