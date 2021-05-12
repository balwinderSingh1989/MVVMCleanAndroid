package com.sample.core.domain.rxcallback

import com.sample.core.domain.remote.Status

interface ResponseCallback<T> {

    fun onResponseSuccess(response: T)

    fun onResponseError(status: Status)

    fun onNetworkError(throwable: Throwable)

    fun onServerError(throwable: Throwable)

    fun onEmptyResponse()

}