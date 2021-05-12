package com.sample.core.data.repository.gavatar.remote

import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.data.repository.gavatar.model.GavatarResponse
import com.sample.core.domain.kotlinflow.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GavatarDataRepository @Inject constructor(
    private val gavatartRemote: GavatarRemote,
    private val dataRemoteConfig: DataRemoteConfig
) : GavatarRepository {


    override fun getGavatar(requestURl: String): Flow<Resource<GavatarResponse.GavatarEntry>> =
        flow {
            emit(Resource.Success(gavatartRemote.getAvatar(requestURl)))
        }

}