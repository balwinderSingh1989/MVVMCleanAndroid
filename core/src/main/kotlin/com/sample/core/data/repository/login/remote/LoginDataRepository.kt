package com.sample.core.data.repository.login.remote

import com.sample.core.data.remote.factory.DataFactory
import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.data.repository.login.model.LoginResponse
import com.sample.core.domain.kotlinflow.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginDataRepository @Inject constructor(
    private val loginRemote: LoginRemote,
    private val dataRemoteConfig: DataRemoteConfig
) : LoginRepository {

    override fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> = flow {
         if (dataRemoteConfig.isMock) {
             delay(2000)  //to simulate network call
            emit(Resource.Success(DataFactory.generateLoginResponse()))
        } else {
            emit(Resource.Success(loginRemote.doLogin(loginRequest)))
        }
    }

}