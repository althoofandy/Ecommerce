package com.example.ecommerce.ui.main.store.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.SearchResponse
import com.example.ecommerce.repos.EcommerceRepository

class SearchViewModel(private val repository: EcommerceRepository) : ViewModel() {
    fun doSearch(token: String, query: String): LiveData<SearchResponse> =
        repository.doSearch(token, query)
}
