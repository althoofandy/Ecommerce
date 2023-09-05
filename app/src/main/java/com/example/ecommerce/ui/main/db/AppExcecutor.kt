package com.example.ecommerce.ui.main.db

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutor {
    val diskIO: Executor = Executors.newSingleThreadExecutor()
    val networkIO: Executor = Executors.newFixedThreadPool(3)
    val mainThread: Executor = MainThreadExecutor()
    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}