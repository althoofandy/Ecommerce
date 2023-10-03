package com.example.ecommerce.ui.prelogin.register

import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.Auth
import com.example.ecommerce.repos.EcommerceRepository

class RegisterViewModel(private val repository: EcommerceRepository) : ViewModel() {

    fun doRegister(token: String, auth: Auth) = repository.doRegister(token, auth)
}
