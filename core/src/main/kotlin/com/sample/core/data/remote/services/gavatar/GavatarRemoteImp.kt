package com.sample.core.data.remote.services.gavatar

import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.data.repository.gavatar.model.GavatarResponse
import com.sample.core.data.repository.gavatar.remote.GavatarRemote
import javax.inject.Inject

class GavatarRemoteImp @Inject constructor(
    private val gavatarService: GavatarService
) : GavatarRemote {

    @Inject
    lateinit var dataRemoteConfig: DataRemoteConfig

    override suspend fun getAvatar(requestUrl: String): GavatarResponse.GavatarEntry {
       return  gavatarService.getAvatar(requestUrl)
    }

}