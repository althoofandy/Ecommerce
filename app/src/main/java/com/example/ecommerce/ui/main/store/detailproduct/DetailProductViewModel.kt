package com.example.ecommerce.ui.main.store.detailproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.model.GetProductDetailResponse
import com.example.ecommerce.core.model.ProductDetailParam
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result

class DetailProductViewModel(
    private val repository: EcommerceRepository,
    private val sharedPref: SharedPref,
) : ViewModel() {
    private val accessToken = sharedPref.getAccessToken() ?: throw Exception("null token")
    private var productId: String? = null

    private val _productDetail =
        MutableLiveData<Result<GetProductDetailResponse>>()
    val productDetail: LiveData<Result<GetProductDetailResponse>> =
        _productDetail

    fun setProductId(idProduct: String?) {
        if (idProduct != productId) {
            productId = idProduct
            fetchData()
        }
    }

    fun fetchData() {
        val query = ProductDetailParam(accessToken, productId)
        repository.getProductDetail(query.token, query.productId!!).observeForever { result ->
            _productDetail.value = result
        }
    }
}
