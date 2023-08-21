package com.example.ecommerce.api

import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.ProfileResponse
import com.example.ecommerce.model.RefreshResponse
import com.example.ecommerce.model.TokenRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Headers("Content-type:application/json; charset=utf-8")
    @POST("login")
     fun doLogin(
        @Header("API_KEY")auth: String,
        @Body user: Auth
    ): Call<DataResponse>

    @Headers("Content-type:application/json; charset=utf-8")
    @POST("register")
     fun doRegister(
        @Header("API_KEY")auth: String,
        @Body user: Auth
    ): Call<DataResponse>

    @Multipart
    @POST("profile")
     fun saveToProfile(
        @Header("Authorization")auth: String,
        @Part userName: MultipartBody.Part,
        @Part userImage: MultipartBody.Part
    ): Call<ProfileResponse>

    @POST("refresh")
    suspend fun refreshToken(
        @Header("API_KEY") auth: String,
        @Body token: TokenRequest
    ): Response<RefreshResponse>



}