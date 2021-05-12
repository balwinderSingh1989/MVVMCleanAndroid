package com.sample.assignment.ui.main

import com.sample.assignment.di.module.ActivityModule
import com.sample.assignment.ui.login.LoginFragment
import com.sample.assignment.ui.profile.ProfileFragment
import com.sample.core.di.scope.PerActivity
import com.sample.core.di.scope.PerFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ActivityModule::class])
abstract class MainActivityModule {

    @PerActivity
    @Binds
    abstract fun bindMainActivty(mainActivity: MainActivity): MainActivity

    @PerFragment
    @ContributesAndroidInjector
    abstract fun providesLoginFragmet(): LoginFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun providedProfileFragment(): ProfileFragment

}