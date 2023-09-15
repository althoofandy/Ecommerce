package com.example.ecommerce.pref

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("EcommercePref", Context.MODE_PRIVATE)

    fun saveAccessToken(accessToken: String?, refreshToken: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(ID_TOKEN, accessToken)
        editor.putString(ID_TOKEN_REFRESH, refreshToken)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(ID_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(ID_TOKEN_REFRESH, null)
    }

    fun saveNameProfile(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ID_NAME, token)
        editor.apply()
    }

    fun saveDarkTheme(isDarkTheme: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("ID_DARK_THEME", isDarkTheme)
        editor.apply()
    }

    fun getNameProfile(): String? {
        return sharedPreferences.getString(ID_NAME, null)
    }

    fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("ID_DARK_THEME", false)
    }

    fun saveFirstInstall(isFisrtInstall: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(ID_FIRST_INSTALL, isFisrtInstall)
        editor.apply()
    }

    fun getIsFirstInstall(): Boolean {
        return sharedPreferences.getBoolean(ID_FIRST_INSTALL, true)
    }

    fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove(ID_TOKEN)
        editor.remove(ID_TOKEN_REFRESH)
        editor.remove(ID_NAME)
        editor.apply()
    }

    companion object {
        private const val ID_TOKEN = "id"
        private const val ID_FIRST_INSTALL = "id_first_install"
        private const val ID_NAME = "id_name"
        private const val ID_TOKEN_REFRESH = "id_token_refresh"
        private const val ID_DARK_THEME = "id_dark_theme"
    }
}
