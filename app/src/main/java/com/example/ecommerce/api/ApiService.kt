package com.example.ecommerce.api

import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.ProfileResponse
import com.example.ecommerce.model.RefreshResponse
import com.example.ecommerce.model.TokenRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("login")
    suspend fun doLogin(
        @Header("API_KEY") auth: String,
        @Body user: Auth
    ): DataResponse

    @POST("register")
    suspend fun doRegister(
        @Header("API_KEY") auth: String,
        @Body user: Auth
    ): DataResponse

    @Multipart
    @POST("profile")
    suspend fun saveToProfile(
        @Header("Authorization") auth: String,
        @Part userName: MultipartBody.Part,
        @Part userImage: MultipartBody.Part
    ): ProfileResponse

    @POST("refresh")
    suspend fun refreshToken(
        @Header("API_KEY") auth: String,
        @Body token: TokenRequest
    ): Response<RefreshResponse>
}