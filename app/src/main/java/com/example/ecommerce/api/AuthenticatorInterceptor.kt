package com.example.ecommerce.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
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

class AuthenticatorInterceptor(private val pref: SharedPref,private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = pref.getRefreshToken().toString()
        synchronized(this) {
            return runBlocking {
                try {
                    val newToken = getToken(refreshToken,context)
                    if (newToken != null) {
                        pref.saveAccessToken(newToken.data?.accessToken,newToken.data?.refreshToken)
                        response.request
                            .newBuilder()
                            .header("Authorization", "Bearer ${newToken.data?.accessToken}")
                            .build()
                    } else {
                        null
                    }
                } catch (error: Throwable) {
                    pref.logout()
                    null
                }
            }

        }
    }


    private suspend fun getToken(token: String,context: Context):RefreshResponse {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.153.125:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val service = retrofit.create(ApiService::class.java)

        val tokenReq = TokenRequest(token!!)
        return service.refreshToken("6f8856ed-9189-488f-9011-0ff4b6c08edc", tokenReq)
    }
}