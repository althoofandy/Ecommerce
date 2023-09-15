package com.example.ecommerce.ui.prelogin.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.ecommerce.model.Auth
import com.example.ecommerce.repos.EcommerceRepository

class LoginViewModel(private val repository: EcommerceRepository) : ViewModel() {
    fun doLogin(token: String, auth: Auth) = repository.doLogin(token, auth)
//    fun getUser(): LiveData<Boolean> {
//        return repository.sharedPref.getUserAppFirstInstall().asLiveData()
//    }


}