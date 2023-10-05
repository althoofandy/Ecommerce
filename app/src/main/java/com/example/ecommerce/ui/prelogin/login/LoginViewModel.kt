package com.example.ecommerce.ui.prelogin.login

import androidx.lifecycle.ViewModel
import com.example.ecommerce.core.model.Auth
import com.example.ecommerce.repos.EcommerceRepository

class LoginViewModel(private val repository: EcommerceRepository) : ViewModel() {
    fun doLogin(token: String, auth: Auth) = repository.doLogin(token, auth)
}
