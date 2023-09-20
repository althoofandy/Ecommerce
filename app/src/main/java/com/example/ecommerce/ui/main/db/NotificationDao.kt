package com.example.ecommerce.ui.main.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecommerce.model.Notification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_table")
    fun getNotification(): LiveData<List<Notification>>

    @Query("SELECT * FROM notification_table WHERE isRead = 0")
    fun getUnreadNotification(): LiveData<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToNotification(notification: Notification)

    @Update
    suspend fun updateNotification(notification: Notification)

    @Query("DELETE FROM notification_table")
    suspend fun deleteAllNotification()
}

