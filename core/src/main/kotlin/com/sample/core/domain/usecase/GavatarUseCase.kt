package com.sample.core.domain.usecase

import com.sample.core.data.repository.gavatar.model.GavatarResponse
import com.sample.core.data.repository.gavatar.remote.GavatarRepository
import com.sample.core.di.qualifier.IoDispatcher
import com.sample.core.domain.FlowUsecase
import com.sample.core.domain.kotlinflow.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GavatarUseCase @Inject constructor(
    private val gavatarRepository: GavatarRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) :
    FlowUsecase<GavatarUseCase.Params, GavatarResponse.GavatarEntry>(ioDispatcher) {


    data class Params constructor(
        val email: String
    ) {
        companion object {
            fun create(
                email: String
            ) =
                Params(email)
        }
    }

    override fun buildUsecase(parameters: Params?): Flow<Resource<GavatarResponse.GavatarEntry>> {
        requireNotNull(parameters).apply {
            return gavatarRepository.getGavatar(
                email
            )
        }
    }

}