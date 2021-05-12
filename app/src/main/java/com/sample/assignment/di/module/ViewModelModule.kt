package com.sample.assignment.di.module

import androidx.lifecycle.ViewModel
import com.sample.assignment.di.key.ViewModelKey
import com.sample.assignment.ui.login.LoginViewModel
import com.sample.assignment.ui.main.MainActivityViewModel
import com.sample.assignment.ui.profile.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(loginViewModel: LoginViewModel)
            : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    fun bindMainViewModel(mainActivityViewModel: MainActivityViewModel)
            : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun bindProfileViewModel(profileViewModel: ProfileViewModel)
            : ViewModel


}


