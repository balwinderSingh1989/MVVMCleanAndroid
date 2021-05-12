package com.sample.assignment

import com.sample.assignment.di.DaggerApplicationComponent
import com.sample.core.data.remote.factory.networkprovider.NetworkProvider
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.logger.AppLogger
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import javax.inject.Inject

class SampleTestApp : DaggerApplication() {

    private lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var networkProvider: NetworkProvider

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppLogger.d(TAG, "app initiated")
        initializeOkHttpClient()
    }


    private fun initializeOkHttpClient() {
        okHttpClient = networkProvider.makeDefaultOkHttpClient()
    }

    /**
     *
     * have used this for glide
     */
    fun getOkHttpClientInstance(): OkHttpClient {
        return okHttpClient
    }
}
