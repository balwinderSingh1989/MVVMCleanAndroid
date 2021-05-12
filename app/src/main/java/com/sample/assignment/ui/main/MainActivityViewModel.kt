package com.sample.assignment.ui.main

import com.sample.assignment.base.BaseViewModel
import com.sample.core.data.cache.AppData
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    appData: AppData
) :
    BaseViewModel() {


    var isUserLoggedIn = appData.hasUserID
        private set


}