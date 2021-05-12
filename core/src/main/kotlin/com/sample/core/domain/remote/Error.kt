package com.sample.core.domain.remote

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: String
)