package com.example.ecommerce.viewmodel.prelogin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.ApiService
import com.example.ecommerce.core.model.ProfileResponse
import com.example.ecommerce.core.model.ProfileResultResponse
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import com.example.ecommerce.ui.prelogin.profile.ProfileViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileViewModelTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: ProfileViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
        viewModel = ProfileViewModel(repository)
    }

    private val userNamePart = MultipartBody.Part.createFormData("userName", "uname")
    private val userImagePart = MultipartBody.Part.createFormData("userImage", "image")

    @Test
    fun `test profileViewModel success`() = runBlocking {
        val expectedResponse = ProfileResponse(
            code = 200,
            data = ProfileResultResponse(
                "uname",
                "image"
            ),
            message = "Success"
        )

        whenever(apiService.saveToProfile("123", userNamePart, userImagePart)).thenReturn(expectedResponse)
        val liveDataResult = viewModel.doProfile("123", userNamePart, userImagePart)
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data)
            }
        }
    }
//    @Test
//    fun `test profileViewModel error`() = runTest {
//         val userNamePart = MultipartBody.Part.createFormData("userName", "uname")
//         val userImagePart = MultipartBody.Part.createFormData("userImage", "image")
//
//        val error = RuntimeException()
//        whenever(apiService.saveToProfile("123", userNamePart,userImagePart)).thenThrow(error)
//        val liveDataResult = viewModel.doProfile("123", userNamePart,userImagePart)
//        liveDataResult.observeForever {
//            if (it is Result.Error) {
//                assertEquals(it, Result.Error(error))
//            }
//        }
//    }
}
