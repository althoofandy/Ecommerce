package com.example.ecommerce.ui.main.store.review

import androidx.lifecycle.ViewModel
import com.example.ecommerce.repos.EcommerceRepository

class ProductReviewViewModel(private val repository: EcommerceRepository):ViewModel() {

    fun getProductReview(token:String, id:String?) = repository.getProductReview(token,id)
}