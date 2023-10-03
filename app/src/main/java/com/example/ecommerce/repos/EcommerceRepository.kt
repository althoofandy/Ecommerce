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
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.GetProductDetailResponse
import com.example.ecommerce.model.GetProductReviewResponse
import com.example.ecommerce.model.GetProductsItemResponse
import com.example.ecommerce.model.Payment
import com.example.ecommerce.model.PaymentMethodResponse
import com.example.ecommerce.model.PaymentResponse
import com.example.ecommerce.model.ProfileResultResponse
import com.example.ecommerce.model.Rating
import com.example.ecommerce.model.RatingResponse
import com.example.ecommerce.model.SearchResponse
import com.example.ecommerce.model.TransactionResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.ui.main.store.paging.ProductPagingSource
import okhttp3.MultipartBody

class EcommerceRepository(
    private val apiService: ApiService,
    private val pref: SharedPref,
) {

    fun doLogin(token: String, auth: Auth): LiveData<Result<DataResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.doLogin(token, auth)
            val resultResponse = response
            pref.saveAccessToken(
                resultResponse.data?.accessToken,
                resultResponse.data?.refreshToken
            )
            pref.saveNameProfile(resultResponse.data?.userName ?: "empty")
            emit(Result.Success(resultResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun doRegister(token: String, auth: Auth): LiveData<Result<DataResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.doRegister(token, auth)
            val resultResponse = response
            pref.saveAccessToken(
                resultResponse.data?.accessToken,
                resultResponse.data?.refreshToken
            )
            emit(Result.Success(resultResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun saveToProfile(
        bearer: String,
        name: MultipartBody.Part,
        image: MultipartBody.Part?,
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

    fun getProductsPaging(
        token: String,
        search: String?,
        brand: String?,
        lowest: Int?,
        highest: Int?,
        sort: String?,
    ): LiveData<PagingData<GetProductsItemResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 1),
            pagingSourceFactory = {
                ProductPagingSource(
                    apiService,
                    "Bearer $token",
                    search,
                    brand,
                    lowest,
                    highest,
                    sort
                )
            }
        ).liveData
    }

    fun doSearch(
        token: String,
        query: String,
    ): LiveData<SearchResponse> = liveData {
        try {
            val response = apiService.doSearch("Bearer $token", query)
            emit(response)
        } catch (e: Exception) {
            throw IllegalArgumentException("nodata")
        }
    }

    fun getProductDetail(token: String, id: String): LiveData<Result<GetProductDetailResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getProductDetail("Bearer $token", id)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    fun getProductReview(token: String, id: String?): LiveData<Result<GetProductReviewResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getProductReview("Bearer $token", id)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    fun getPaymentMethods(token: String): LiveData<Result<PaymentMethodResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getPaymentMethods("Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun doBuyProducts(token: String, payment: Payment): LiveData<Result<PaymentResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.doBuyProducts("Bearer $token", payment)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    fun getTransactionHistory(token: String): LiveData<Result<TransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getTransactionHistory("Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun doGiveRating(token: String, rating: Rating): LiveData<Result<RatingResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.doGiveRating("Bearer $token", rating)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
