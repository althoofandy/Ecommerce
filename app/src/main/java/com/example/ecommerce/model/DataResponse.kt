package com.example.ecommerce.model

import com.google.gson.annotations.SerializedName

data class Auth(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("firebaseToken")
    val firebaseToken: String
)

data class DataResponse(
    @SerializedName("id")
    val code: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ResultResponse?
)

data class ResultResponse(
    @SerializedName("userName")
    val userName: String,

    @SerializedName("userImage")
    val userImage: String,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("expiresAt")
    val expiresAt: Int
)

data class ProfileResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProfileResultResponse?
)

data class ProfileResultResponse(
    @SerializedName("userName")
    val userName: String,

    @SerializedName("userImage")
    val userImage: String? = null
)

data class TokenRequest(
    @field:SerializedName("token")
    val token: String
)

data class RefreshResponse(
    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: RefreshDataResponse?
)

data class RefreshDataResponse(
    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("refreshToken")
    val refreshToken: String,

    @field:SerializedName("expiresAt")
    val expiresAt: Int
)





