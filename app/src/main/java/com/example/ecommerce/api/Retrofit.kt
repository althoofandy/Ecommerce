package com.example.ecommerce.api

import android.content.Context
import com.example.ecommerce.pref.SharedPref
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit(private val context: Context) {
    val pref = SharedPref(context)
    val authenticatorInterceptor = AuthenticatorInterceptor(pref)
    fun getApiService(): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticatorInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.20.217:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)

    }
}