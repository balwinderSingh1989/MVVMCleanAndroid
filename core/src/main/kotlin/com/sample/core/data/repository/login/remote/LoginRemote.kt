package com.sample.core.data.repository.login.remote

import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.data.repository.login.model.LoginResponse
import io.reactivex.Single

interface LoginRemote {
    suspend  fun doLogin(
        loginRequest: LoginRequest
    ): LoginResponse
}