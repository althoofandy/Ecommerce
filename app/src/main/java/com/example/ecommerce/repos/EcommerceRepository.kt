package com.example.ecommerce.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.api.Result
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.ProfileResultResponse
import com.example.ecommerce.model.ResultResponse
import com.example.ecommerce.pref.SharedPref
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
                resultResponse!!.refreshToken
            )
            pref.saveNameProfile(resultResponse!!.userName)
            emit(Result.Success(resultResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun doRegister(token: String, auth: Auth): LiveData<Result<ResultResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.doRegister(token, auth)
            if (response != null) {
                val resultResponse = response.data
                pref.saveAccessToken(
                    resultResponse!!.accessToken,
                    resultResponse!!.refreshToken
                )
                emit(Result.Success(resultResponse))
            } else {
                Log.d("GAGAL MANING", "HEHE")
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun saveToProfile(
        bearer: String,
        name: MultipartBody.Part,
        image: MultipartBody.Part
    ): LiveData<Result<ProfileResultResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.saveToProfile("Bearer $bearer", name, image)
            if (response != null) {
                val resultResponse = response.data
                pref.saveNameProfile(resultResponse!!.userName)
                emit(Result.Success(resultResponse))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

}