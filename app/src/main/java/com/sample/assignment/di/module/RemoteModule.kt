package com.sample.assignment.di.module

import com.sample.core.data.remote.factory.networkprovider.NetworkProvider
import com.sample.core.data.remote.services.gavatar.GavatarRemoteImp
import com.sample.core.data.remote.services.gavatar.GavatarService
import com.sample.core.data.remote.services.login.LoginRemoteImp
import com.sample.core.data.remote.services.login.LoginService
import com.sample.core.data.repository.gavatar.remote.GavatarRemote
import com.sample.core.data.repository.login.remote.LoginRemote
import com.sample.core.di.scope.PerApplication
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteModule {

    @Module
    companion object {

        @PerApplication
        @Provides
        @JvmStatic
        fun provideAppTokenService(networkProvider: NetworkProvider) =
            networkProvider.create(LoginService::class.java)

        @PerApplication
        @Provides
        @JvmStatic
        fun providesGavatarService(networkProvider: NetworkProvider) =
            networkProvider.create(GavatarService::class.java)

    }


    @Binds
    abstract fun bindsLoginRemoteImpl(loginRemoteImp: LoginRemoteImp): LoginRemote

    @Binds
    abstract fun bindsGavatarRemoteImpl(gavatarRemoteImp: GavatarRemoteImp): GavatarRemote
}