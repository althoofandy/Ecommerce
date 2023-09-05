package com.example.ecommerce.ui.main

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    fun formatRupiah(amount: Double?): String? {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount)
    }
    fun formatRupiah(amount: Int?): String? {
        return formatRupiah(amount?.toDouble())
    }
}