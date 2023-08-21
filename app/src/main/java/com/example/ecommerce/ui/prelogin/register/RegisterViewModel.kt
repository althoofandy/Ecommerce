package com.example.ecommerce.ui.prelogin.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.repos.EcommerceRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: EcommerceRepository):
    ViewModel() {
    val registerResponse: LiveData<DataResponse?> = repository.register
    fun doRegister(token : String,auth: Auth) {
        viewModelScope.launch {
            repository.doRegister(token,auth)
        }
    }
}