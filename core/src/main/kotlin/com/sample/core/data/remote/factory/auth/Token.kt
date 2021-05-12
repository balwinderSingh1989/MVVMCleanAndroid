package com.sample.core.data.remote.factory.auth

import com.sample.core.di.scope.PerApplication
import com.sample.core.utility.extensions.empty
import javax.inject.Inject

@PerApplication
class Token @Inject constructor(
) {


    private var appToken = String.empty

    fun setToken(token: String) {
        appToken = token
    }

    fun getToken(): String {
        return appToken
    }

    fun clearToken() {
        appToken = String.empty
    }

    fun hasToken() = appToken.isNotEmpty()


}