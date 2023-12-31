package com.example.ecommerce.ui.main.store.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.model.GetProductReviewResponse
import com.example.ecommerce.core.model.ProductReviewParam
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result

class ProductReviewViewModel(
    private val repository: EcommerceRepository,
    private val sharedPref: SharedPref,
) : ViewModel() {
    private val accessToken = sharedPref.getAccessToken() ?: throw Exception("null token")
    private val _param = MutableLiveData(ProductReviewParam(accessToken))
    val param: LiveData<ProductReviewParam> = _param

    fun setProductId(idProduct: String?) {
        _param.value = _param.value?.copy(productId = idProduct)
    }

    val getReviewProduct: LiveData<Result<GetProductReviewResponse>> = param.switchMap { query ->
        repository.getProductReview(query.token, query.productId)
    }
}
