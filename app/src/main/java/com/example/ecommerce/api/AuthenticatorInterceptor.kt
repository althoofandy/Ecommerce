package com.example.ecommerce.api

import android.content.ContentValues
import android.util.Log
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.RefreshResponse
import com.example.ecommerce.model.TokenRequest
import com.example.ecommerce.pref.SharedPref
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthenticatorInterceptor(private val pref: SharedPref) : Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request? {
//        val newAccessToken = pref.getRefreshToken()
//        return runBlocking {
//            val newToken = getToken(newAccessToken)
//            newToken.enqueue(object : Callback<RefreshResponse>{
//                override fun onResponse(
//                    call: Call<RefreshResponse>,
//                    response: retrofit2.Response<RefreshResponse>
//                ) {
//
//                }
//
//                override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {
//
//                }
//
//
//            }            })
//
//        if (newAccessToken != null) {
//            val updatedRequest = response.request.newBuilder()
//                .header("Authorization", "Bearer $newAccessToken")
//                .build()
//
//            pref.saveAccessToken(newAccessToken, pref.getRefreshToken() ?: "")
//            return updatedRequest
//        }
//        return null
//    }
//
//        fun getToken(token : String): Call<RefreshResponse> {
//            val loggingInterceptor =
//                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//            val client = OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build()
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://172.17.20.217:5000/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build()
//            val service = retrofit.create(ApiService::class.java)
//            val tokenReq = TokenRequest(token)
//            return service.refreshToken(tokenReq)
//        }

}