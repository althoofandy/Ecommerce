package com.example.ecommerce.ui.main.transaction.successpayment

import androidx.lifecycle.ViewModel
import com.example.ecommerce.core.model.Rating
import com.example.ecommerce.repos.EcommerceRepository

class SuccessPaymentViewModel(private val repository: EcommerceRepository) : ViewModel() {
    fun doGiveRate(token: String, rate: Rating) = repository.doGiveRating(token, rate)
}
