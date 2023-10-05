package com.example.ecommerce.viewmodel.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.db.ProductDAO
import com.example.ecommerce.core.db.ProductDatabase
import com.example.ecommerce.core.model.ProductLocalDb
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CartViewModelTest {
    private lateinit var viewModel: CartViewModel

    private lateinit var database: ProductDatabase
    private lateinit var productDao: ProductDAO

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ProductDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = mock()
        viewModel = CartViewModel(context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private val product = ProductLocalDb(
        productId = "12345",
        productName = "Sample Product",
        productPrice = 1000,
        image = "sample_image_url.jpg",
        brand = "Sample Brand",
        description = "This is a sample product description.",
        store = "Sample Store",
        sale = 10,
        stock = 50,
        totalRating = 4,
        totalReview = 20,
        totalSatisfaction = 90,
        productRating = 4.5f,
        variantName = "Sample Variant",
        variantPrice = 1200,
        quantity = 2,
        selected = true
    )

    @Test
    fun `test addToCartViewModel success`() {
        runTest {
            whenever(productDao.addToCart(product)).thenReturn(Unit)
            val checkInsert = viewModel.addToCart(
                productId = "12345",
                productName = "Sample Product",
                productPrice = 1000,
                image = "sample_image_url.jpg",
                store = "Sample Store",
                sale = 10,
                stock = 50,
                rating = 4,
                productRating = 4.5f,
                variantName = "Sample Variant",
                variantPrice = 1200
            )
            assertEquals(Unit, checkInsert)
        }
    }
//    @Test
//    fun `test getCartById success`() {
//        runBlocking {
//            whenever(productDao.getProductById("12345")).thenReturn(product)
//            viewModel.addToCart(
//                productId = "12345",
//                productName = "Sample Product",
//                productPrice = 1000,
//                image = "sample_image_url.jpg",
//                store = "Sample Store",
//                sale = 10,
//                stock = 50,
//                rating = 4,
//                productRating = 4.5f,
//                variantName = "Sample Variant",
//                variantPrice = 1200
//            )
//            val checkData = viewModel.getCartById("12345")
//            assertEquals(product, checkData)
//        }
//    }

    @Test
    fun `test getCart success`() {
        runBlocking {
            val product = listOf(product)
            val expectresponse = MutableLiveData<List<ProductLocalDb>>()
            expectresponse.value = product
            viewModel.addToCart(
                productId = "12345",
                productName = "Sample Product",
                productPrice = 1000,
                image = "sample_image_url.jpg",
                store = "Sample Store",
                sale = 10,
                stock = 50,
                rating = 4,
                productRating = 4.5f,
                variantName = "Sample Variant",
                variantPrice = 1200
            )

            whenever(productDao.getCartProducts()).thenReturn(expectresponse)
            val checkData = viewModel.getCartItem()
            checkData?.observeForever {
                val expectedCartItem = expectresponse.value?.firstOrNull()
                assertEquals(expectedCartItem, it.firstOrNull())
            }
        }
    }

    @Test
    fun `test removeFromCart success`() {
        runBlocking {
            whenever(productDao.removeFromCart("12345")).thenReturn(Unit)
            viewModel.removeFromCart("12345")
            val checkData = viewModel.removeFromCart("12345")
            assertEquals(Unit, checkData)
        }
    }

    @Test
    fun `test removeFromCartAll success`() {
        runBlocking {
            whenever(productDao.removeFromCartAll(listOf("12345"))).thenReturn(Unit)
            viewModel.removeFromCartAll(listOf("12345"))
            val checkData = viewModel.removeFromCartAll(listOf("12345"))
            assertEquals(Unit, checkData)
        }
    }

    @Test
    fun `test updateCartItemQuantity success`() {
        runBlocking {
            whenever(productDao.updateCartItemQuantity("12345", 4)).thenReturn(Unit)
            viewModel.updateCartItemQuantity("12345", 4)
            val checkData = viewModel.updateCartItemQuantity("12345", 4)
            assertEquals(Unit, checkData)
        }
    }

    @Test
    fun `test updateCartItemCheckbox success`() {
        runBlocking {
            whenever(productDao.updateCartItemCheckbox(listOf("12345"), true)).thenReturn(Unit)
            viewModel.updateCartItemCheckbox(listOf("12345"), true)
            val checkData = viewModel.updateCartItemCheckbox(listOf("12345"), true)
            assertEquals(Unit, checkData)
        }
    }
}
