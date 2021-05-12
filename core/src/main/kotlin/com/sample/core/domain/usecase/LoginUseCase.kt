package com.sample.core.domain.usecase

import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.data.repository.login.model.LoginResponse
import com.sample.core.data.repository.login.remote.LoginRepository
import com.sample.core.di.qualifier.IoDispatcher
import com.sample.core.domain.FlowUsecase
import com.sample.core.domain.kotlinflow.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    @IoDispatcher  ioDispatcher: CoroutineDispatcher
) :
    FlowUsecase<LoginUseCase.Params, LoginResponse>(ioDispatcher) {


    data class Params constructor(
        val loginRequest: LoginRequest
    ) {
        companion object {
            fun create(
                loginrequest: LoginRequest
            ) =
                Params(loginrequest)
        }
    }

    override fun buildUsecase(parameters: Params?): Flow<Resource<LoginResponse>> {
        requireNotNull(parameters).apply {
            return loginRepository.doLogin(
                loginRequest = loginRequest
            )
        }
    }


}