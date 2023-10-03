package com.example.ecommerce.viewmodel.prelogin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.api.Result
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.ResultResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.prelogin.login.LoginViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: LoginViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = LoginViewModel(repository)
    }

    private val request = Auth(
        "test@gmail.com",
        "12345678",
        "token"
    )

    @Test
    fun `test loginViewModel success`() = runBlocking {
        val expectedResponse = DataResponse(
            code = 200,
            data = ResultResponse(
                "dummy",
                "dummy",
                "dummy",
                "dummy",
                600
            ),
            message = "Success"
        )
        whenever(apiService.doLogin("123", request)).thenReturn(expectedResponse)
        val liveDataResult = viewModel.doLogin("123", request)
        liveDataResult.observeForever {
            if (it is Result.Success) {
                assertEquals(200, it.data.code)
            }
        }
    }

    @Test
    fun `test loginViewModel error`() = runBlocking {
        val error = RuntimeException()
        whenever(apiService.doLogin("123", request)).thenThrow(error)
        val liveDataResult = viewModel.doLogin("123", request)
        liveDataResult.observeForever {
            if (it is Result.Error) {
                assertEquals(it, Result.Error(error))
            }
        }
    }
}
