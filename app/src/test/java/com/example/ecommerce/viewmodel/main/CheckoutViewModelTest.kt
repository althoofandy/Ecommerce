package com.example.ecommerce.viewmodel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.api.Result
import com.example.ecommerce.model.Payment
import com.example.ecommerce.model.PaymentDataResponse
import com.example.ecommerce.model.PaymentItem
import com.example.ecommerce.model.PaymentResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.transaction.checkout.CheckoutViewModel
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
class CheckoutViewModelTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: CheckoutViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = CheckoutViewModel(repository)
    }

    @Test
    fun doBuyProducts() {
        runBlocking {
            val expectedResponse = PaymentResponse(
                200,
                "Success",
                PaymentDataResponse(
                    "123",
                    true,
                    "28 Jun 2023",
                    "15:00",
                    "BNI VIRTUAL",
                    10000,
                    "bagus",
                    5
                )
            )
            whenever(
                apiService.doBuyProducts(
                    "123",
                    Payment(
                        "dummy",
                        listOf(PaymentItem("dummy", "dummy", 5))
                    )
                )
            ).thenReturn(expectedResponse)
            val livedataResult = apiService.doBuyProducts(
                "123",
                Payment(
                    "dummy",
                    listOf(PaymentItem("dummy", "dummy", 5))
                )
            )
            assertEquals(expectedResponse, livedataResult)
        }
    }

    @Test
    fun doBuyProductsError() {
        runBlocking {
            val error = RuntimeException()
            whenever(
                apiService.doBuyProducts(
                    "123",
                    Payment(
                        "dummy",
                        listOf(PaymentItem("dummy", "dummy", 5))
                    )
                )
            ).thenThrow(error)
            val livedataResult = viewModel.doBuyProducts(
                "123",
                Payment(
                    "dummy",
                    listOf(PaymentItem("dummy", "dummy", 5))
                )
            )
            livedataResult.observeForever {
                if(it is Result.Error){
                    assertEquals(error, it.exception)
                }
            }

        }
    }
}
