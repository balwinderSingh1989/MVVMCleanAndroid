package com.sample.assignment.di.module

import com.sample.assignment.di.ViewModelBuilder
import com.sample.assignment.ui.main.MainActivity
import com.sample.assignment.ui.main.MainActivityModule
import com.sample.core.di.scope.PerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class, MainActivityModule::class
        ]
    )
    abstract fun contributesMainctivity(): MainActivity


}

