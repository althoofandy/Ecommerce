package com.example.ecommerce.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.ApiService
import com.example.ecommerce.core.model.Auth
import com.example.ecommerce.core.model.DataResponse
import com.example.ecommerce.core.model.GetProductDetailItemResponse
import com.example.ecommerce.core.model.GetProductDetailResponse
import com.example.ecommerce.core.model.GetProductReviewItemResponse
import com.example.ecommerce.core.model.GetProductReviewResponse
import com.example.ecommerce.core.model.Payment
import com.example.ecommerce.core.model.PaymentDataResponse
import com.example.ecommerce.core.model.PaymentItem
import com.example.ecommerce.core.model.PaymentMethodCategoryResponse
import com.example.ecommerce.core.model.PaymentMethodItemResponse
import com.example.ecommerce.core.model.PaymentMethodResponse
import com.example.ecommerce.core.model.PaymentResponse
import com.example.ecommerce.core.model.ProductVariant
import com.example.ecommerce.core.model.ProfileResponse
import com.example.ecommerce.core.model.ProfileResultResponse
import com.example.ecommerce.core.model.Rating
import com.example.ecommerce.core.model.RatingResponse
import com.example.ecommerce.core.model.ResultResponse
import com.example.ecommerce.core.model.SearchResponse
import com.example.ecommerce.core.model.TransactionDataResponse
import com.example.ecommerce.core.model.TransactionResponse
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class RepositoryTest {
    private lateinit var repository: EcommerceRepository
    private lateinit var apiService: ApiService
    private lateinit var sharedPref: SharedPref

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        apiService = mock()
        sharedPref = mock()
        repository = EcommerceRepository(apiService, sharedPref)
    }

    private val request = Auth(
        "test@gmail.com",
        "12345678",
        "token"
    )

    @Test
    fun `test login success`() = runTest {
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
        val liveDataResult = repository.doLogin("123", request)
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse, it.data)
                println(it.data)
            }
        }
    }

    @Test
    fun `test register success`() = runTest {
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
        val liveDataResult = repository.doRegister("123", request)
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse, it.data)
                println(it.data)
            }
        }
    }

    @Test
    fun `test saveToProfile success`() = runTest {
        val expectedResponse = ProfileResponse(
            code = 200,
            data = ProfileResultResponse(
                "dummy",
                "dummy"
            ),
            message = "Success"
        )

        val userNamePart = MultipartBody.Part.createFormData("userName", "uname")
        val userImagePart = MultipartBody.Part.createFormData("userImage", "image")

        whenever(apiService.saveToProfile("123", userNamePart, userImagePart)).thenReturn(
            expectedResponse
        )
        val liveDataResult = repository.saveToProfile("123", userNamePart, userImagePart)
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data)
            }
        }
    }

    @Test
    fun `test doSearch success`() = runTest {
        val expectedResponse = SearchResponse(
            code = 200,
            data = listOf("dummy"),
            message = "Success"
        )

        whenever(apiService.doSearch("123", "dummy")).thenReturn(expectedResponse)
        val liveDataResult = apiService.doSearch("123", "dummy")
        Assert.assertEquals(expectedResponse, liveDataResult)
    }

    @Test
    fun `test getProductDetail success`() = runTest {
        val expectedResponse = GetProductDetailResponse(
            code = 200,
            message = "Success",
            data = GetProductDetailItemResponse(
                productId = "12345",
                productName = "Sample Product",
                productPrice = 999,
                image = listOf(
                    "https://example.com/image1.jpg"
                ),
                brand = "Sample Brand",
                description = "This is a sample product description.",
                store = "Sample Store",
                sale = 20,
                stock = 100,
                totalRating = 4,
                totalReview = 50,
                totalSatisfaction = 90,
                productRating = 4.5f,
                productVariant = listOf(
                    ProductVariant("dummy", 1000),
                )
            )
        )

        whenever(apiService.getProductDetail("123", "12345")).thenReturn(expectedResponse)
        val liveDataResult = repository.getProductDetail("123", "12345")
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data.data)
            }
        }
    }

    @Test
    fun `test doBuyProduct success`() = runTest {
        val expectedResponse = PaymentResponse(
            code = 200,
            message = "mantap",
            data = PaymentDataResponse(
                invoiceId = "INV12345",
                status = true,
                date = "2023-09-27",
                time = "14:30:00",
                payment = "Credit Card",
                total = 100,
                review = "This is a dummy review",
                rating = 4
            )
        )
        whenever(
            apiService.doBuyProducts(
                "123",
                Payment("dummy", listOf(PaymentItem("dummy", "dummy", 5)))
            )
        ).thenReturn(expectedResponse)
        val liveDataResult = repository.doBuyProducts(
            "123",
            Payment("dummy", listOf(PaymentItem("dummy", "dummy", 5)))
        )
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data.data)
            }
        }
    }

    @Test
    fun `test getProductReview success`() = runTest {
        val expectedResponse = GetProductReviewResponse(
            code = 200,
            message = "mantap",
            data = listOf(
                GetProductReviewItemResponse(
                    "dummy",
                    "image.png",
                    4,
                    "bagus sekali"
                )
            )
        )
        whenever(apiService.getProductReview("123", "321")).thenReturn(expectedResponse)
        val liveDataResult = repository.getProductReview("123", "321")
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data.data)
            }
        }
    }

    @Test
    fun `test getPaymentMethods success`() = runTest {
        val expectedResponse = PaymentMethodResponse(
            code = 200,
            message = "mantap",
            data = listOf(
                PaymentMethodCategoryResponse(
                    "dummy",
                    listOf(
                        PaymentMethodItemResponse(
                            "BNI",
                            "bni.png",
                            true
                        )
                    )
                )
            )
        )
        whenever(apiService.getPaymentMethods("123")).thenReturn(expectedResponse)
        val liveDataResult = repository.getPaymentMethods("123")
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data.data)
            }
        }
    }

    @Test
    fun `test getTransactionHistory success`() = runTest {
        val expectedResponse = TransactionResponse(
            code = "200",
            message = "mantap",
            data = listOf(
                TransactionDataResponse(
                    invoiceId = "INV12345",
                    status = true,
                    date = "2023-09-27",
                    time = "14:30:00",
                    payment = "Credit Card",
                    total = 100,
                    items = listOf(
                        PaymentItem("Item1", "16GB", 2),
                        PaymentItem("Item2", "16GB", 1),
                        PaymentItem("Item3", "16GB", 3)
                    ),
                    rating = 4,
                    review = "This is a dummy review",
                    image = "https://example.com/image.jpg",
                    name = "John Doe"
                )
            )
        )
        whenever(apiService.getTransactionHistory("123")).thenReturn(expectedResponse)
        val liveDataResult = repository.getTransactionHistory("123")
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.data, it.data.data)
            }
        }
    }

    @Test
    fun `test doGiveRating success`() = runTest {
        val expectedResponse = RatingResponse(
            code = "200",
            message = "mantap",
        )
        whenever(apiService.doGiveRating("123", Rating("dummyId", 4, "mantap ngab"))).thenReturn(expectedResponse)
        val liveDataResult = repository.doGiveRating("123", Rating("dummyId", 4, "mantap ngab"))
        liveDataResult.observeForever {
            if (it is Result.Success) {
                Assert.assertEquals(expectedResponse.code, it.data.code)
            }
        }
    }
}
