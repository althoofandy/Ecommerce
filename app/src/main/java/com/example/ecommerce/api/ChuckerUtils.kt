package com.example.ecommerce.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager

object ChuckerUtils {
    private fun createChuckerCollector(context: Context): ChuckerCollector {
        return ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
    }
    fun getChuckerInterceptor(context: Context): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(createChuckerCollector(context))
            .maxContentLength(250_000L)
            .redactHeaders("API_KEY", "6f8856ed-9189-488f-9011-0ff4b6c08edc")
            .alwaysReadResponseBody(true)
            .build()
    }

}