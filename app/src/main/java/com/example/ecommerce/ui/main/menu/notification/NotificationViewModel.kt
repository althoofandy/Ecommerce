package com.example.ecommerce.ui.main.menu.notification

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.core.db.ProductDatabase
import com.example.ecommerce.core.model.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NotificationViewModel(context: Context) : ViewModel() {
    private var productDb: ProductDatabase? = ProductDatabase.getDatabase(context)
    private var notificationDao = productDb?.notificationDao()

    fun getAllNotification(): LiveData<List<Notification>>? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                notificationDao?.getNotification()
            }
        }
    }

    fun addNotification(notification: Notification) {
        return runBlocking {
            withContext(Dispatchers.IO) {
                notificationDao?.addToNotification(notification)
            }
        }
    }

    fun updateNotification(notification: Notification) {
        viewModelScope.launch {
            notificationDao?.updateNotification(notification)
        }
    }
}
