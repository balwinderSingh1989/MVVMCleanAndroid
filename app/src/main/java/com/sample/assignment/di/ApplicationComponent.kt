package com.sample.assignment.di

import android.content.Context
import com.sample.assignment.SampleTestApp
import com.sample.assignment.di.module.*
import com.sample.core.di.qualifier.ApplicationContext
import com.sample.core.di.scope.PerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule


@PerApplication
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        NetworkModule::class,
        DataModule::class,
        RemoteModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        CoroutinesModule::class

    ]
)
interface ApplicationComponent : AndroidInjector<SampleTestApp> {

//    @Component.Builder
//    interface Factory {
//        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
//
//        @BindsInstance
//        abstract fun appContext(@ApplicationContext context: Context)
//
//    }

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SampleTestApp>() {

        @BindsInstance
        abstract fun appContext(@ApplicationContext context: Context)

        override fun seedInstance(instance: SampleTestApp) {
            appContext(instance)
        }
    }


}

