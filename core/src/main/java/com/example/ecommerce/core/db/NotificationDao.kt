package com.example.ecommerce.core.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecommerce.core.model.Notification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_table")
    fun getNotification(): LiveData<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToNotification(notification: Notification)

    @Update
    suspend fun updateNotification(notification: Notification)
}
