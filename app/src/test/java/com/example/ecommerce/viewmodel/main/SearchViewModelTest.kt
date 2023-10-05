package com.example.ecommerce.viewmodel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.ApiService
import com.example.ecommerce.core.model.SearchResponse
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.search.SearchViewModel
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
class SearchViewModelTest {

    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: SearchViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun `test doSearch success`() {
        runBlocking {
            val expectedResponse = SearchResponse(
                200,
                "Success",
                listOf("")
            )
            whenever(apiService.doSearch("123", "macbook")).thenReturn(expectedResponse)
            val liveDataResult = apiService.doSearch("123", "macbook")
            assertEquals(expectedResponse, liveDataResult)
        }
    }
}
