package com.example.ecommerce.core.di

import com.example.ecommerce.core.model.Auth
import com.example.ecommerce.core.model.DataResponse
import com.example.ecommerce.core.model.GetProductDetailResponse
import com.example.ecommerce.core.model.GetProductResponse
import com.example.ecommerce.core.model.GetProductReviewResponse
import com.example.ecommerce.core.model.Payment
import com.example.ecommerce.core.model.PaymentMethodResponse
import com.example.ecommerce.core.model.PaymentResponse
import com.example.ecommerce.core.model.ProfileResponse
import com.example.ecommerce.core.model.Rating
import com.example.ecommerce.core.model.RatingResponse
import com.example.ecommerce.core.model.RefreshResponse
import com.example.ecommerce.core.model.SearchResponse
import com.example.ecommerce.core.model.TokenRequest
import com.example.ecommerce.core.model.TransactionResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    suspend fun doLogin(
        @Header("API_KEY") auth: String,
        @Body user: Auth,
    ): DataResponse

    @POST("register")
    suspend fun doRegister(
        @Header("API_KEY") auth: String,
        @Body user: Auth,
    ): DataResponse

    @Multipart
    @POST("profile")
    suspend fun saveToProfile(
        @Header("Authorization") auth: String,
        @Part userName: MultipartBody.Part,
        @Part userImage: MultipartBody.Part?,
    ): ProfileResponse

    @POST("refresh")
    suspend fun refreshToken(
        @Header("API_KEY") auth: String,
        @Body token: TokenRequest,
    ): RefreshResponse

    @POST("products")
    suspend fun getProducts(
        @Header("Authorization") auth: String,
        @Query("search") search: String?,
        @Query("brand") brand: String?,
        @Query("lowest") lowest: Int?,
        @Query("highest") highest: Int?,
        @Query("sort") sort: String?,
        @Query("limit") limit: Int?,
        @Query("page") page: Int?,
    ): GetProductResponse

    @POST("search")
    suspend fun doSearch(
        @Header("Authorization") auth: String,
        @Query("query") query: String,
    ): SearchResponse

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
    ): GetProductDetailResponse

    @GET("review/{id}")
    suspend fun getProductReview(
        @Header("Authorization") auth: String,
        @Path("id") id: String?,
    ): GetProductReviewResponse

    // PAYMENT and TRANSACTION
    @GET("payment")
    suspend fun getPaymentMethods(
        @Header("Authorization") auth: String,
    ): PaymentMethodResponse

    @POST("fulfillment")
    suspend fun doBuyProducts(
        @Header("Authorization") auth: String,
        @Body payment: Payment,
    ): PaymentResponse

    @POST("rating")
    suspend fun doGiveRating(
        @Header("Authorization") auth: String,
        @Body rating: Rating,
    ): RatingResponse

    @GET("transaction")
    suspend fun getTransactionHistory(
        @Header("Authorization") auth: String,
    ): TransactionResponse
}
