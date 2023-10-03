package com.example.ecommerce.viewmodel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.api.Result
import com.example.ecommerce.model.Rating
import com.example.ecommerce.model.RatingResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.transaction.successpayment.SuccessPaymentViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SuccessPaymentViewModelTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: SuccessPaymentViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = SuccessPaymentViewModel(repository)
    }

    @Test
    fun doGiveRate() {
        runBlocking {
            val expectedResponse = RatingResponse(
                "200",
                "Berhasil"
            )

            whenever(apiService.doGiveRating("123", Rating("123", 5, "mantap"))).thenReturn(
                expectedResponse
            )
            val result = viewModel.doGiveRate("123", Rating("123", 5, "mantap"))
            result.observeForever {
                if (it is Result.Success) {
                    assertEquals(expectedResponse, it.data)
                }
            }
        }
    }

    @Test
    fun doGiveRateError() {
        runBlocking {
            val error = RuntimeException()
            whenever(apiService.doGiveRating("123", Rating("123", 5, "mantap"))).thenThrow(
                error
            )
            val result = viewModel.doGiveRate("123", Rating("123", 5, "mantap"))
            result.observeForever {
                if (it is Result.Error) {
                    assertEquals(error, it.exception)
                }
            }
        }
    }
}
