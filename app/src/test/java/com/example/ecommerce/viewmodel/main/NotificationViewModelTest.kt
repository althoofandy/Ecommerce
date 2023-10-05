package com.example.ecommerce.viewmodel.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.example.ecommerce.Utils.MainDispatcherRule
import com.example.ecommerce.core.db.NotificationDao
import com.example.ecommerce.core.model.Notification
import com.example.ecommerce.ui.main.menu.notification.NotificationViewModel
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
class NotificationViewModelTest {
    private lateinit var viewModel: NotificationViewModel
    private lateinit var notificationDao: NotificationDao

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        notificationDao = mock()
        viewModel = NotificationViewModel(context)
    }

    private val notification = Notification(
        id = "1",
        type = "News",
        date = "2023-09-27",
        title = "Lorem Ipsum",
        body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        image = "https://example.com/image.jpg",
        isRead = false
    )

    @Test
    fun getAllNotification() {
        runBlocking {
            val expected = MutableLiveData<List<Notification>>()
            expected.value = listOf(notification)
            viewModel.addNotification(notification)
            whenever(notificationDao.getNotification()).thenReturn(expected)

            val getData = viewModel.getAllNotification()
            getData?.observeForever {
                val expectedResponse = expected.value?.firstOrNull()
                assertEquals(expectedResponse, it.firstOrNull())
            }
        }
    }

//    @Test
//    fun addNotification() {
//        runTest {
//            whenever(notificationDao.addToNotification(notification)).thenReturn(Unit)
//            val input = viewModel.addNotification(notification)
//            assertEquals(Unit, input)
//        }
//    }

    @Test
    fun updateNotification() {
        runBlocking {
            whenever(notificationDao.updateNotification(notification)).thenReturn(Unit)
            val input = viewModel.updateNotification(notification)
            assertEquals(Unit, input)
        }
    }
}
