package com.example.ecommerce.viewmodel.prelogin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.ApiService
import com.example.ecommerce.core.model.Auth
import com.example.ecommerce.core.model.DataResponse
import com.example.ecommerce.core.model.ResultResponse
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import com.example.ecommerce.ui.prelogin.register.RegisterViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterViewModelTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: RegisterViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = RegisterViewModel(repository)
    }

    private val request = Auth(
        "test@gmail.com",
        "12345678",
        "token"
    )

    @Test
    fun `test registerViewModel success`() = runBlocking {
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

        whenever(apiService.doRegister("123", request)).thenReturn(expectedResponse)
        val liveDataResult = viewModel.doRegister("123", request)
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(200, it.data.code)
            }
        }
    }

    @Test
    fun `test registerViewModel error`() = runBlocking {
        val error = RuntimeException()
        whenever(apiService.doRegister("123", request)).thenThrow(error)
        val liveDataResult = viewModel.doRegister("123", request)
        liveDataResult.observeForever {
            if (it is Result.Error) {
                Assert.assertEquals(it, Result.Error(error))
            }
        }
    }
}
