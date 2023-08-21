package com.example.ecommerce.ui.prelogin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.ProfileResponse
import com.example.ecommerce.repos.EcommerceRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val repository: EcommerceRepository):
    ViewModel() {
    val profileResponse: LiveData<ProfileResponse?> = repository.profile
    fun doProfile(token : String,name: MultipartBody.Part, image: MultipartBody.Part) {
        viewModelScope.launch {
            repository.saveToProfile(token,name,image)
        }
    }
}