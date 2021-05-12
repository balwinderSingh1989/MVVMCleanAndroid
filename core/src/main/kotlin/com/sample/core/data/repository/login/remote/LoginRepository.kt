package com.sample.core.data.repository.login.remote

import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.data.repository.login.model.LoginResponse
import com.sample.core.domain.kotlinflow.Resource
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun doLogin(
        loginRequest: LoginRequest
    ): Flow<Resource<LoginResponse>>


}