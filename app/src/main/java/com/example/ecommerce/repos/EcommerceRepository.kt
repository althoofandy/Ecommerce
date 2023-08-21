package com.example.ecommerce.repos

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.ProfileResponse
import com.example.ecommerce.pref.SharedPref
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EcommerceRepository(private val apiService: ApiService,
    private val pref: SharedPref) {

    private val _login = MutableLiveData<DataResponse?>()
    val login: LiveData<DataResponse?> = _login!!

     fun doLogin(token : String, user : Auth){
        apiService.doLogin(token,user).enqueue(object : Callback<DataResponse> {
            override fun onResponse(
                call: Call<DataResponse>,
                response: Response<DataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Response : ${response.body()}}")
                    pref.saveAccessToken(response.body()!!.data!!.accessToken, response.body()!!.data!!.refreshToken)
                    pref.saveNameProfile(response.body()!!.data!!.userName)
                    _login.value = response.body()
                }else{
                    _login.value = null
                }
            }
            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                Log.d(TAG, "error : ${t.message.toString()}")
                _login.value = null
            }
        })
    }
    private val _register = MutableLiveData<DataResponse?>()
    val register: LiveData<DataResponse?> = _register
     fun doRegister(token : String,user : Auth){
        apiService.doRegister(token,user).enqueue(object : Callback<DataResponse> {
            override fun onResponse(
                call: Call<DataResponse>,
                response: Response<DataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Response : ${response.body()}}")
                    pref.saveAccessToken(response.body()!!.data!!.accessToken, response.body()!!.data!!.refreshToken)
                    _register.value = response.body()
                }else{
                    _register.value = null
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                Log.d(TAG, "error : ${t.message.toString()}")
                _register.value = null
            }
        })

    }
    private val _profile = MutableLiveData<ProfileResponse?>()
    val profile: LiveData<ProfileResponse?> = _profile
     fun saveToProfile(bearer: String, name: MultipartBody.Part, image:MultipartBody.Part){
        apiService.saveToProfile("Bearer $bearer",name,image).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    pref.saveNameProfile(response.body()!!.data!!.userName)
                    _profile.value = response.body()
                }else{
                    _profile.value = null
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d(TAG, "Response : ${t.message.toString()}")
                _profile.value = null
            }
        })

    }

}