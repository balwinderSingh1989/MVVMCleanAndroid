package com.sample.core.domain.remote

import com.google.gson.annotations.SerializedName


/**
 * In case server API maintains status and error as part of API
 * response.
 */
open class BaseResponse {


    @SerializedName("status")
    var status: Status? = null

    @SerializedName("error")
    var error: Error? = null

    @Suppress("UNCHECKED_CAST")
    fun <B : BaseResponse> withStatusCode(code: String): B {
        status = Status.withCode(code)
        return this as B
    }


}