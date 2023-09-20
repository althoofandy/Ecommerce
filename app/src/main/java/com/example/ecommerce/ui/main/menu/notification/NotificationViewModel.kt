package com.example.ecommerce.ui.main.menu.notification

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Notification
import com.example.ecommerce.ui.main.db.ProductDatabase
import kotlinx.coroutines.launch

class NotificationViewModel(context: Context): ViewModel() {
    private var productDb: ProductDatabase? = ProductDatabase.getDatabase(context)
    private var notificationDao = productDb?.notificationDao()

    fun getAllNotification():LiveData<List<Notification>>?{
        return notificationDao?.getNotification()
    }

    fun addNotification(notification: Notification){
        notificationDao?.addToNotification(notification)
    }

    fun updateNotification(notification: Notification){
        viewModelScope.launch {
            notificationDao?.updateNotification(notification)
        }
    }
}