package com.sample.assignment.di.module

import android.content.Context
import com.sample.assignment.SampleTestApp
import com.sample.core.di.qualifier.ApplicationContext
import com.sample.core.di.scope.PerApplication
import dagger.Binds
import dagger.Module

@Module
abstract class ApplicationModule {

    @ApplicationContext
    @PerApplication
    @Binds
    abstract fun bindApplication(application: SampleTestApp): Context

}