package com.example.ecommerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.detailproduct.DetailProductViewModel
import com.example.ecommerce.ui.main.store.mainStore.StoreViewModel
import com.example.ecommerce.ui.main.store.review.ProductReviewViewModel
import com.example.ecommerce.ui.main.store.search.SearchViewModel
import com.example.ecommerce.ui.prelogin.login.LoginViewModel
import com.example.ecommerce.ui.prelogin.profile.ProfileViewModel
import com.example.ecommerce.ui.prelogin.register.RegisterViewModel

class ViewModelFactory(
    private val repository: EcommerceRepository,
    private val sharedPref: SharedPref
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(StoreViewModel::class.java) -> {
                StoreViewModel(repository,sharedPref) as T
            }

            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailProductViewModel::class.java) -> {
                DetailProductViewModel(repository,sharedPref) as T
            }
            modelClass.isAssignableFrom(ProductReviewViewModel::class.java) -> {
                ProductReviewViewModel(repository,sharedPref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}