package com.example.ecommerce.ui.main.store.detailproduct

import androidx.lifecycle.ViewModel
import com.example.ecommerce.repos.EcommerceRepository

class DetailProductViewModel(private val repository: EcommerceRepository):ViewModel() {
    fun getDetailProduct(token:String,id:String? = null) = repository.getProductDetail(token,id!!)

}