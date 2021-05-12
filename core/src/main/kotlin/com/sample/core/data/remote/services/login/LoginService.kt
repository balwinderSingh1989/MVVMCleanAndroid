package com.sample.core.data.remote.services.login

import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.data.repository.login.model.LoginResponse
import io.reactivex.Single
import retrofit2.http.*


interface LoginService {

    @POST("session/new")
    suspend fun doLogin(
        @Body loginRequest: LoginRequest
    ): LoginResponse

}