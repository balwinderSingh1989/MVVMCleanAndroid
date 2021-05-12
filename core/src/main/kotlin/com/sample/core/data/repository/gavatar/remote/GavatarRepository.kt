package com.sample.core.data.repository.gavatar.remote

import com.sample.core.data.repository.gavatar.model.GavatarResponse
import com.sample.core.domain.kotlinflow.Resource
import kotlinx.coroutines.flow.Flow

interface GavatarRepository {

    fun getGavatar(
        requestURl: String
    ): Flow<Resource<GavatarResponse.GavatarEntry>>


}