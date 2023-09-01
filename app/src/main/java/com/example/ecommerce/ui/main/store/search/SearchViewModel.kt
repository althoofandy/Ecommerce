package com.example.ecommerce.ui.main.store.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.api.Result
import com.example.ecommerce.repos.EcommerceRepository

class SearchViewModel(private val repository: EcommerceRepository):ViewModel() {
    fun doSearch(token:String, query : String): LiveData<List<String>> = repository.doSearch(token,query)
}