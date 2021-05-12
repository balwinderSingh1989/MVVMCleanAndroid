package com.sample.core.data.remote.services.gavatar

import com.sample.core.data.repository.gavatar.model.GavatarResponse
import retrofit2.http.*


interface GavatarService {

    @GET("https://www.gravatar.com/{email}.json")
   suspend fun getAvatar(
        @Path("email") emailID : String
    ): GavatarResponse.GavatarEntry

}