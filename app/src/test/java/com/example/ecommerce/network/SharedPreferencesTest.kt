package com.example.ecommerce.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.SharedPref
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SharedPreferencesTest {
    private lateinit var sharedPref: SharedPref

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPref = SharedPref(context)
    }

    private val idToken = "123"
    private val idInstall = true
    private val idName = "dummy"
    private val idTokenRefresh = "123"

    @Test
    fun `test saveAccessToken success`() {
        sharedPref.saveAccessToken(idToken, idTokenRefresh)
        val checkData = sharedPref.getAccessToken()
        assertEquals(idToken, checkData)
    }

    @Test
    fun `test saveNameProfile success`() {
        sharedPref.saveNameProfile(idName)
        val checkData = sharedPref.getNameProfile()
        assertEquals(idName, checkData)
    }

    @Test
    fun `test saveDarkTheme success`() {
        sharedPref.saveDarkTheme(true)
        val checkData = sharedPref.getDarkTheme()
        assertEquals(true, checkData)
    }

    @Test
    fun `test saveFirstInstall success`() {
        sharedPref.saveFirstInstall(idInstall)
        val checkData = sharedPref.getIsFirstInstall()
        assertEquals(idInstall, checkData)
    }

    @Test
    fun `test logout success`() {
        sharedPref.saveAccessToken(idToken, idTokenRefresh)
        sharedPref.logout()
        val checkData = sharedPref.getAccessToken()
        assertEquals(null, checkData)
    }

    @Test
    fun `test getData success`() {
        sharedPref.saveAccessToken(idToken, idTokenRefresh)
        sharedPref.saveDarkTheme(true)
        sharedPref.saveNameProfile(idName)
        sharedPref.saveFirstInstall(idInstall)

        val checkAccessTokenData = sharedPref.getAccessToken()
        val checkRefreshTokenData = sharedPref.getRefreshToken()
        val checkDarkThemeData = sharedPref.getDarkTheme()
        val checkNameProfileData = sharedPref.getNameProfile()
        val checkFirstInstallData = sharedPref.getIsFirstInstall()

        assertEquals(idToken, checkAccessTokenData)
        assertEquals(idTokenRefresh, checkRefreshTokenData)
        assertEquals(true, checkDarkThemeData)
        assertEquals(idName, checkNameProfileData)
        assertEquals(idInstall, checkFirstInstallData)
    }
}
