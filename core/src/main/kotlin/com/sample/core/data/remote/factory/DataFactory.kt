package com.sample.core.data.remote.factory

import com.sample.core.data.repository.login.model.LoginResponse
import com.sample.core.utility.constants.JWT_AUTH_HEADER
import io.reactivex.Single
import retrofit2.Response
import java.net.HttpURLConnection


object DataFactory {

    fun generateLoginResponse() =
        LoginResponse(
            userId = "133131",
            token = "some_random_token_hex"
        )

}