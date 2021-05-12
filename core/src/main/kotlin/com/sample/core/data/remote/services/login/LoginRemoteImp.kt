package com.sample.core.data.remote.services.login

import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.data.repository.login.remote.LoginRemote
import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.data.repository.login.model.LoginResponse
import javax.inject.Inject

class LoginRemoteImp @Inject constructor(
    private val loginService: LoginService
) : LoginRemote {

    @Inject
    lateinit var dataRemoteConfig: DataRemoteConfig

    override suspend  fun doLogin(loginRequest: LoginRequest): LoginResponse {
        return loginService.doLogin(loginRequest)

    }

}