package com.sample.core.data.repository.gavatar.remote

import com.sample.core.data.repository.gavatar.model.GavatarResponse

interface GavatarRemote {
    suspend  fun getAvatar(
        requestUrl: String
    ): GavatarResponse.GavatarEntry
}