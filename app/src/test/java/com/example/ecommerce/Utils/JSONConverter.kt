package com.example.ecommerce.Utils

import okhttp3.mockwebserver.MockResponse
import java.io.InputStreamReader

object JSONConverter {

    fun readStringFromFile(fileName: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
        val reader = InputStreamReader(inputStream).readText()

        return reader
    }

    fun createMockResponse(fileName: String) = MockResponse()
        .setResponseCode(200)
        .setBody(readStringFromFile(fileName))
}
