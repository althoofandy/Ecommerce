package com.example.ecommerce.viewmodel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.ApiService
import com.example.ecommerce.core.model.PaymentItem
import com.example.ecommerce.core.model.TransactionDataResponse
import com.example.ecommerce.core.model.TransactionResponse
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import com.example.ecommerce.ui.main.transaction.TransactionViewModel
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
class TransactionViewModelTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: TransactionViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = TransactionViewModel(repository)
    }

    @Test
    fun getTransactionHistory() {
        runBlocking {
            val expectedResponse = TransactionResponse(
                "200",
                "Success",
                listOf(
                    TransactionDataResponse(
                        "132",
                        true,
                        "123",
                        "123",
                        "123",
                        10000,
                        listOf(
                            PaymentItem(
                                "123",
                                "555",
                                5
                            )
                        ),
                        5,
                        "Bagus",
                        "qwe.png",
                        "qwe"
                    )
                )
            )
            whenever(apiService.getTransactionHistory("123")).thenReturn(expectedResponse)
            val livedataResult = viewModel.getTransactionHistory("123")
            livedataResult.observeForever {
                if (it is Result.Success) {
                    assertEquals(expectedResponse.data, it.data.data)
                }
            }
        }
    }

    @Test
    fun getTransactionHistoryFailed() {
        runBlocking {
            val error = RuntimeException()
            whenever(apiService.getTransactionHistory("123")).thenThrow(error)
            val livedataResult = viewModel.getTransactionHistory("123")
            livedataResult.observeForever {
                if (it is Result.Error) {
                    assertEquals(error, it.exception)
                }
            }
        }
    }
}
