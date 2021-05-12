package com.sample.assignment.di.module

import com.sample.assignment.BuildConfig
import com.sample.core.data.remote.factory.auth.Token
import com.sample.core.data.remote.factory.networkprovider.NetworkProvider
import com.sample.core.di.scope.PerApplication
import com.sample.core.utility.manager.SessionManager
import dagger.Module
import dagger.Provides

@Module
class NetworkModule {

    @Provides
    @PerApplication
    fun provideServiceFactory(
        token: Token,
        sessionManager: SessionManager,
    ) =
        NetworkProvider(
            endPoint = BuildConfig.END_POINT,
            sessionManager = sessionManager,
            token = token
        )

}