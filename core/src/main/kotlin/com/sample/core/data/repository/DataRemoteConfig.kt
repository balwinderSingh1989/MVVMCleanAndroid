package com.sample.core.data.repository

import com.sample.core.di.scope.PerApplication
import javax.inject.Inject


@PerApplication
open class DataRemoteConfig @Inject constructor(
    val isMock: Boolean
)