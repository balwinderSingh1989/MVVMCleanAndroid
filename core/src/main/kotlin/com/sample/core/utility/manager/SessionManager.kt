package com.sample.core.utility.manager

import com.sample.core.data.remote.factory.auth.Token
import com.sample.core.di.scope.PerApplication
import com.sample.core.utility.logger.AppLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@PerApplication
class SessionManager @Inject constructor(
    private val token: Token,
) {

    private val sessionPublisher: PublishSubject<String> =
        PublishSubject.create<String>()


    fun subscribeSession(onNext: Consumer<String>) =
        sessionPublisher
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext)!!

    fun sessionExpired() {
        clearSession()
        AppLogger.e("Session Manager", "ON SESSION EXPIRED")
        sessionPublisher.onNext("on session expired")
    }

    fun clearSession() {
        token.clearToken()
    }

    fun hasToken() = token.hasToken()

    fun setToken(tokenValue: String) {
        token.setToken(
            tokenValue
        )
    }
}