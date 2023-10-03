package com.example.ecommerce.ui.main.store.mainStore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.ecommerce.MainActivity
import com.example.ecommerce.model.ProductParam
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository

class StoreViewModel(private val repository: EcommerceRepository, sharedPref: SharedPref) :
    ViewModel() {
    private val accesToken = sharedPref.getAccessToken() ?: MainActivity().logOut()
    private val _param = MutableLiveData(ProductParam(accesToken.toString()))
    val param: LiveData<ProductParam> = _param
    fun setSearch(
        search: String? = null,
    ) {
        _param.value = _param.value?.copy(
            search = search,
        )
    }

    fun setQuery(
        brand: String? = null,
        lowest: Int? = null,
        highest: Int? = null,
        sort: String? = null,
    ) {
        _param.value = _param.value?.copy(
            brand = brand,
            lowest = lowest,
            highest = highest,
            sort = sort
        )
    }

    fun resetParam() {
        _param.value = _param.value?.copy(
            search = null,
            brand = null,
            lowest = null,
            highest = null,
            sort = null
        )
    }

    val products = param.switchMap { query ->
        repository.getProductsPaging(
            query.token,
            query.search,
            query.brand,
            query.lowest,
            query.highest,
            query.sort
        )
    }.cachedIn(viewModelScope)
}
