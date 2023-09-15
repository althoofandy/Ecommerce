package com.example.ecommerce.ui.main.store.detailproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.ecommerce.model.GetProductDetailItemResponse
import com.example.ecommerce.model.GetProductDetailResponse
import com.example.ecommerce.model.ProductDetailParam
import com.example.ecommerce.model.ProductParam
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailProductViewModel(
    private val repository: EcommerceRepository
    ,private val sharedPref: SharedPref
) : ViewModel() {

    private val accessToken = sharedPref.getAccessToken() ?: throw Exception("null token")
    private val _param = MutableLiveData(ProductDetailParam(accessToken))
    val param: LiveData<ProductDetailParam> = _param


    fun setProductId(idProduct: String?) {
        _param.value = _param.value?.copy(productId = idProduct)
    }

    val getDetailProduct: LiveData<com.example.ecommerce.api.Result<GetProductDetailResponse>> = param.switchMap { query ->
        repository.getProductDetail(query.token, query.productId!!)
    }
}