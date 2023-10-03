package com.example.ecommerce.network

import com.example.ecommerce.Utils.JSONConverter
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.model.Auth
import com.example.ecommerce.model.DataResponse
import com.example.ecommerce.model.GetProductDetailItemResponse
import com.example.ecommerce.model.GetProductDetailResponse
import com.example.ecommerce.model.GetProductResponse
import com.example.ecommerce.model.GetProductReviewItemResponse
import com.example.ecommerce.model.GetProductReviewResponse
import com.example.ecommerce.model.GetProductsItemResponse
import com.example.ecommerce.model.GetProductsResultResponse
import com.example.ecommerce.model.Payment
import com.example.ecommerce.model.PaymentDataResponse
import com.example.ecommerce.model.PaymentItem
import com.example.ecommerce.model.PaymentMethodCategoryResponse
import com.example.ecommerce.model.PaymentMethodItemResponse
import com.example.ecommerce.model.PaymentMethodResponse
import com.example.ecommerce.model.PaymentResponse
import com.example.ecommerce.model.ProductVariant
import com.example.ecommerce.model.ProfileResponse
import com.example.ecommerce.model.ProfileResultResponse
import com.example.ecommerce.model.Rating
import com.example.ecommerce.model.RatingResponse
import com.example.ecommerce.model.RefreshDataResponse
import com.example.ecommerce.model.RefreshResponse
import com.example.ecommerce.model.ResultResponse
import com.example.ecommerce.model.SearchResponse
import com.example.ecommerce.model.TokenRequest
import com.example.ecommerce.model.TransactionDataResponse
import com.example.ecommerce.model.TransactionResponse
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class ApiServiceTest {
    private lateinit var apiService: ApiService

    private var mockWebServer: MockWebServer = MockWebServer()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test login success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("login.json")
        mockWebServer.enqueue(mockedResponse)

        val actualResponse = apiService.doLogin("123", Auth("test@gmail.com", "12345678", ""))
        val expectedResponse = DataResponse(
            code = 200,
            message = "OK",
            ResultResponse(
                userName = "uname",
                userImage = "image",
                accessToken = "accesstoken",
                refreshToken = "refreshtoken",
                expiresAt = 600
            )
        )
        assertEquals(expectedResponse.message, actualResponse.message)
    }

    @Test
    fun `test register success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("register.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.doRegister(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                Auth("test2@gmail.com", "12345678", "123")
            )
        val expectedResponse = DataResponse(
            code = 200,
            message = "OK",
            ResultResponse(
                userName = "uname",
                userImage = "image",
                accessToken = "accesstoken",
                refreshToken = "refreshtoken",
                expiresAt = 600
            )
        )

        assertEquals(expectedResponse.message, actualResponse.message)
    }

    @Test
    fun `test profile success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("register.json")

        mockWebServer.enqueue(mockedResponse)

        val userNamePart = MultipartBody.Part.createFormData("userName", "uname")
        val userImagePart = MultipartBody.Part.createFormData("userImage", "image")

        val actualResponse =
            apiService.saveToProfile(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                userNamePart,
                userImagePart
            )
        val expectedResponse = ProfileResponse(
            code = 200,
            message = "OK",
            ProfileResultResponse(
                userName = "uname",
                userImage = "image",
            )
        )

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test getProduct success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("product.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.getProducts(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                "",
                "",
                null,
                null,
                "",
                null,
                null
            )
        val expectedResponse = GetProductResponse(
            code = 200,
            message = "OK",
            GetProductsResultResponse(
                10,
                10,
                1,
                3,
                listOf(
                    GetProductsItemResponse(
                        "productId",
                        "Lenovo",
                        5000,
                        "image1",
                        "Lenovo",
                        "LenovoStore",
                        2,
                        4F
                    )
                )
            )
        )

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test refresh success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("refresh.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.refreshToken(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                TokenRequest("123")
            )
        val expectedResponse = RefreshResponse(
            code = 200,
            message = "OK",
            RefreshDataResponse(
                "accesstoken",
                "refreshtoken",
                600,
            )
        )

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test search success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("search.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.doSearch(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                "Lenovo"
            )
        val expectedResponse = SearchResponse(
            code = 200,
            message = "OK",
            listOf(
                "Lenovo"
            )
        )

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test product detail success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("detailproduct.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.getProductDetail(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                "productId"
            )
        val expectedResponse = GetProductDetailResponse(
            code = 200,
            message = "OK",
            GetProductDetailItemResponse(
                "productId",
                "ASUS",
                5000,
                image = listOf("image1", "image2", "image3"),
                "Asus",
                "ASUS",
                "AsusStore",
                12,
                2,
                7,
                5,
                100,
                5F,
                productVariant = listOf(
                    ProductVariant("16GB", 0),
                    ProductVariant("32GB", 5000)
                )
            )
        )

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test product review success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("productreview.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.getProductReview(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                "productId"
            )
        val expectedResponse = GetProductReviewResponse(
            code = 200,
            message = "OK",
            listOf(
                GetProductReviewItemResponse("John", "imageJohn", 4, "Lorem"),
                GetProductReviewItemResponse("Doe", "imageDoe", 5, "Lorem"),
            )
        )
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test payment success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("payment.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.getPaymentMethods(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc"
            )
        val expectedResponse = PaymentMethodResponse(
            code = 200,
            message = "OK",
            listOf(
                PaymentMethodCategoryResponse(
                    "Transfer Virtual Account",
                    listOf(
                        PaymentMethodItemResponse("BCA Virtual Account", "image1", true),
                        PaymentMethodItemResponse("BNI Virtual Account", "image2", true)
                    )
                ),
                PaymentMethodCategoryResponse(
                    "Transfer Bank",
                    listOf(
                        PaymentMethodItemResponse("Bank BCA", "image3", true),
                        PaymentMethodItemResponse("Bank BNI", "image4", true)
                    )
                ),
                PaymentMethodCategoryResponse(
                    "Pembayaran Instan",
                    listOf(
                        PaymentMethodItemResponse("GoPay", "image5", true),
                        PaymentMethodItemResponse("OVO", "image6", true)

                    )
                )
            )
        )
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test fulfillment success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("fulfillment.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.doBuyProducts(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                Payment("123", listOf(PaymentItem("productId", "16GB", 2)))
            )
        val expectedResponse = PaymentResponse(
            code = 200,
            message = "OK",
            PaymentDataResponse(
                "invoiceId",
                true,
                "09 Jun 2023",
                "08:53",
                "Bank BCA",
                10000,
                null,
                null
            )
        )
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test rating success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("rating.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.doGiveRating(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                Rating("123", 4, "mantap")
            )
        val expectedResponse = RatingResponse(
            code = "200",
            message = "Fulfillment rating and review success"
        )
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test transaction success`() = runTest {
        val mockedResponse = JSONConverter.createMockResponse("transaction.json")

        mockWebServer.enqueue(mockedResponse)

        val actualResponse =
            apiService.getTransactionHistory(
                "6f8856ed-9189-488f-9011-0ff4b6c08edc"
            )
        val expectedResponse = TransactionResponse(
            code = "200",
            message = "OK",
            listOf(
                TransactionDataResponse(
                    "invoiceId",
                    true,
                    "09 Jun 2023",
                    "09:05",
                    "Bank BCA",
                    10000,
                    listOf(
                        PaymentItem("productId", "16GB", 2)
                    ),
                    4,
                    "good",
                    "image",
                    "asus"
                ),

            )
        )
        assertEquals(expectedResponse, actualResponse)
    }
}
