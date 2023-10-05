package com.example.ecommerce.network

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.db.NotificationDao
import com.example.ecommerce.core.db.ProductDAO
import com.example.ecommerce.core.db.ProductDatabase
import com.example.ecommerce.core.db.WishlistDao
import com.example.ecommerce.core.model.Notification
import com.example.ecommerce.core.model.ProductLocalDb
import com.example.ecommerce.core.model.WishlistProduct
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomDatabaseTest {
    private lateinit var database: ProductDatabase
    private lateinit var productDao: ProductDAO
    private lateinit var wishlistDao: WishlistDao
    private lateinit var notificationDao: NotificationDao

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ProductDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = database.productDao()
        wishlistDao = database.wishListDao()
        notificationDao = database.notificationDao()
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
    fun `test addToCart success`() {
        runTest {
            productDao.addToCart(product)
            productDao.getCartProducts().observeForever {
                assertThat(it, equalTo(product))
            }
        }
    }

    @Test
    fun `test getProductCartById success`() {
        runTest {
            productDao.addToCart(product)
            val productById = productDao.getProductById("12345")
            assertEquals(product, productById)
        }
    }

    @Test
    fun `test updateCartItemQuantity success`() {
        runTest {
            productDao.addToCart(product)
            productDao.updateCartItemQuantity("12345", 4)
            val newProduct = productDao.getProductById("12345")
            assertEquals(4, newProduct?.quantity)
        }
    }

    @Test
    fun `test updateCartItemCheckbox success`() {
        runTest {
            productDao.addToCart(product)
            productDao.updateCartItemCheckbox(listOf("12345"), false)
            val updatedProduct = productDao.getProductById("12345")
            assertEquals(false, updatedProduct?.selected)
        }
    }

    @Test
    fun `test removeFromCart success`() {
        runTest {
            productDao.addToCart(product)
            productDao.removeFromCart("12345")
            productDao.getCartProducts().observeForever {
                assertEquals(0, it.size)
            }
        }
    }

    @Test
    fun `test removeAllFromCart success`() {
        runTest {
            productDao.addToCart(product)
            productDao.removeFromCartAll(listOf("12345"))
            productDao.getCartProducts().observeForever {
                assertEquals(0, it.size)
            }
        }
    }

    private val productWishlist = WishlistProduct(
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
    fun `test addToWishlist success`() {
        runTest {
            wishlistDao.addToWishlist(productWishlist)
            wishlistDao.getWishlistProducts().observeForever {
                assertThat(it, equalTo(productWishlist))
            }
        }
    }

    @Test
    fun `test removeWishlist success`() {
        runTest {
            wishlistDao.addToWishlist(productWishlist)
            wishlistDao.removeWishlist("12345")
            wishlistDao.getWishlistProducts().observeForever {
                assertEquals(0, it.size)
            }
        }
    }

    @Test
    fun `test getProductWishlistById success`() {
        runTest {
            wishlistDao.addToWishlist(productWishlist)
            val checkWishlist = wishlistDao.getProductWishlistById("12345")
            assertEquals(productWishlist, checkWishlist)
        }
    }

    private val notification = Notification(
        "123",
        "promo",
        "24",
        "dummy",
        "dummy",
        "imagedummy.jpg",
        false
    )

    @Test
    fun `test addToNotification success`() {
        notificationDao.addToNotification(notification)
        notificationDao.getNotification().observeForever {
            assertThat(it, equalTo(notification))
        }
    }

    @Test
    fun `test updateNotification success`() {
        runTest {
            notificationDao.addToNotification(notification)
            notificationDao.updateNotification(
                Notification(
                    "123",
                    "promo",
                    "24",
                    "dummy",
                    "dummy",
                    "imagedummy.jpg",
                    true
                )
            )
            notificationDao.getNotification().observeForever {
                assertEquals(true, it.filter { it.isRead })
            }
        }
    }
}
