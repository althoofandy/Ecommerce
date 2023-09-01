package com.example.ecommerce.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.api.Result
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.GetProductDetailItemResponse
import com.example.ecommerce.model.GetProductDetailResponse
import com.example.ecommerce.model.GetProductReviewResponse
import com.example.ecommerce.model.GetProductsItemResponse
import com.example.ecommerce.model.ProfileResultResponse
import com.example.ecommerce.model.ResultResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.ui.main.store.paging.ProductPagingSource
import okhttp3.MultipartBody

class EcommerceRepository(
    private val apiService: ApiService,
    private val pref: SharedPref
) {

    fun doLogin(token: String, auth: Auth): LiveData<Result<ResultResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.doLogin(token, auth)
            val resultResponse = response.data
            pref.saveAccessToken(
                resultResponse!!.accessToken,
                resultResponse.refreshToken
            )
            pref.saveNameProfile(resultResponse.userName)
            emit(Result.Success(resultResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun doRegister(token: String, auth: Auth): LiveData<Result<ResultResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.doRegister(token, auth)
            val resultResponse = response.data
            pref.saveAccessToken(
                resultResponse!!.accessToken,
                resultResponse.refreshToken
            )
            emit(Result.Success(resultResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun saveToProfile(
        bearer: String,
        name: MultipartBody.Part,
        image: MultipartBody.Part?
    ): LiveData<Result<ProfileResultResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.saveToProfile("Bearer $bearer", name, image)
            val resultResponse = response.data
            pref.saveNameProfile(resultResponse!!.userName)
            emit(Result.Success(resultResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    fun getProductsPaging(token: String,
                          search: String?,
                          brand: String?,
                          lowest: Int?,
                          highest: Int?,
                          sort: String?,
                          ): LiveData<PagingData<GetProductsItemResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 1),
            pagingSourceFactory = { ProductPagingSource(apiService, "Bearer $token",search,brand,lowest,highest,sort) }
        ).liveData
    }

    fun doSearch(
        token: String,
        query: String
    ): LiveData<List<String>> = liveData {
        try {
            val response = apiService.doSearch("Bearer $token",query)
            val resultResponse = response.data
            emit(resultResponse)
        } catch (e: Exception) {
            throw IllegalArgumentException("nodata")
        }
    }

    fun getProductDetail(token:String,id: String): LiveData<Result<GetProductDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductDetail("Bearer $token",id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun getProductReview(token:String,id: String?): LiveData<Result<GetProductReviewResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductReview("Bearer $token",id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}