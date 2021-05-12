package com.sample.core.data.repository.login.model

import com.google.gson.annotations.Expose
import com.sample.core.domain.remote.BaseResponse
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @Expose @SerializedName("userId") val userId: String,
    @Expose @SerializedName("token") val token: String
) : BaseResponse()
