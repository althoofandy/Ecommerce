package com.example.ecommerce.api

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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthenticatorInterceptor(private val pref: SharedPref) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request {
        synchronized(this) {
            return runBlocking {
                val refreshToken = pref.getRefreshToken()
                val newToken = getToken(refreshToken!!)
                if (!newToken.isSuccessful || newToken.body() == null) {
                    pref.logout()
                }
                newToken.body().let {
                    pref.saveAccessToken(it?.data!!.accessToken, it.data.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${it.data.accessToken}")
                        .build()
                }
            }
        }
    }

    private suspend fun getToken(token: String): retrofit2.Response<RefreshResponse> {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.20.217:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val service = retrofit.create(ApiService::class.java)

        val tokenReq = TokenRequest(token)
        return service.refreshToken("6f8856ed-9189-488f-9011-0ff4b6c08edc", tokenReq)
    }
}