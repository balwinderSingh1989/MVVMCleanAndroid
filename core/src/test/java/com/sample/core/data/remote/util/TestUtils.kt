@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
package com.sample.core.data.remote.util

import okhttp3.mockwebserver.MockResponse
import java.io.File

object TestUtils {

    fun createMockResponse(fileName: String? = null, responseCode: Int): MockResponse {
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(responseCode)
        if (!fileName.isNullOrEmpty()) {
            mockResponse.setBody(getJson(fileName))
        }
        return mockResponse
    }

    fun getJson(path: String): String {
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }

}