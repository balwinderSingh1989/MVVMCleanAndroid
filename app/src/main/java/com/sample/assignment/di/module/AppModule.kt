package com.sample.assignment.di.module

import com.sample.assignment.BuildConfig
import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.di.scope.PerApplication
import dagger.Module
import dagger.Provides

@Module
abstract class AppModule {


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
}