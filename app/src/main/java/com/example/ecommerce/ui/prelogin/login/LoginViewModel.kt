package com.example.ecommerce.ui.prelogin.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.repos.EcommerceRepository

class LoginViewModel(private val repository: EcommerceRepository) : ViewModel() {
    val loginResponse: LiveData<DataResponse?> = repository.login
     fun doLogin(token: String, auth: Auth) {
            repository.doLogin(token, auth)

    }
}