package com.sample.assignment.di.module

import com.sample.assignment.BuildConfig
import com.sample.assignment.helper.UiThread
import com.sample.assignment.util.cache.AppDataImpl
import com.sample.core.data.cache.AppData
import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.data.repository.gavatar.remote.GavatarDataRepository
import com.sample.core.data.repository.gavatar.remote.GavatarRepository
import com.sample.core.data.repository.login.remote.LoginDataRepository
import com.sample.core.data.repository.login.remote.LoginRepository
import com.sample.core.di.scope.PerApplication
import com.sample.core.domain.executor.PostExecutionThread
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class DataModule {

    @Module
    companion object {
        @PerApplication
        @Provides
        @JvmStatic
        fun provideConfig() =
            DataRemoteConfig(
                isMock = BuildConfig.FLAVOR.contains("Mock")
            )
    }

    @PerApplication
    @Binds
    abstract fun bindPostExecutionThread(uiThread: UiThread): PostExecutionThread


    @PerApplication
    @Binds
    abstract fun bindsLoginRepository(
        loginDataRepository: LoginDataRepository
    ): LoginRepository



    @PerApplication
    @Binds
    abstract fun bindsGavatarRepository(
        gavatarDataRepository: GavatarDataRepository
    ): GavatarRepository


    @PerApplication
    @Binds
    abstract fun bindAppDAta(
        appDataImpl: AppDataImpl
    ): AppData


}