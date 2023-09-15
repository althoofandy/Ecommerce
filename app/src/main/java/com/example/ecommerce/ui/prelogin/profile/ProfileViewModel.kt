package com.example.ecommerce.ui.prelogin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.ecommerce.repos.EcommerceRepository
import okhttp3.MultipartBody

class ProfileViewModel(private val repository: EcommerceRepository) : ViewModel() {

    fun doProfile(token: String, name: MultipartBody.Part, image: MultipartBody.Part?) =
        repository.saveToProfile(token, name, image)
//    fun getUserToken():LiveData<String>{
//        return repository.sharedPref.getUserToken().asLiveData()
//    }
}