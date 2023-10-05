package com.example.ecommerce.ui

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    object Loading : Result<Nothing>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
