package com.sample.core.domain

import com.sample.core.domain.executor.ThreadExecutor
import com.sample.core.domain.remote.BaseError
import com.sample.core.domain.remote.Status
import com.sample.core.domain.rxcallback.CallbackWrapper
import com.google.gson.Gson
import com.sample.core.domain.executor.PostExecutionThread
import com.sample.core.domain.remote.BaseResponse
import com.sample.core.domain.remote.exception.*
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.extensions.safeGet
import com.sample.core.utility.logger.AppLogger
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.TimeoutException

abstract class SingleUseCase<T : BaseResponse, Params> {

    private val threadScheduler: Scheduler

    private val postExecutionThread: PostExecutionThread

    constructor(postExecutionThread: PostExecutionThread) {
        this.postExecutionThread = postExecutionThread
        threadScheduler = Schedulers.io()
    }

    constructor(
        threadExecutor: ThreadExecutor,
        postExecutionThread: PostExecutionThread
    ) {
        threadScheduler = Schedulers.from(threadExecutor)
        this.postExecutionThread = postExecutionThread
    }

    abstract fun buildUseCase(params: Params? = null): Single<T>

    open fun execute(
        callbackWrapper: CallbackWrapper<T>?,
        params: Params? = null
    ): Disposable? {
        if (callbackWrapper == null) {
            return null
        }

        val single = buildUseCase(params)
            .subscribeOn(threadScheduler)
            .observeOn(postExecutionThread.scheduler())


        return single.subscribe({ result ->
            callbackWrapper.onResponseSuccess(result)
        }, { exception ->

            when (exception) {

                is HttpException -> {
                    AppLogger.e(
                        "$TAG exceptionCode",
                        exception.code().toString()
                    )

                    if (exception.code() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        callbackWrapper.onEmptyResponse()
                    } else {
                        exception.response()!!.errorBody()?.let {
                            val errorText = it.string().safeGet()
                            AppLogger.e("onRetrofitException", errorText)
                            handleResponseError(
                                errorText = errorText,
                                callbackWrapper = callbackWrapper
                            )
                        }
                    }
                }

                is SessionExpireException -> {
                    //it has been handled in sessionManager already
                }

                is EOFException -> {
                    callbackWrapper.onEmptyResponse()
                }
                is ServerNotAvailableException -> {
                    callbackWrapper.onServerError(exception)
                }
                is HTTPNotFoundException -> {
                    callbackWrapper.onResponseError(
                        Status(
                            HttpURLConnection.HTTP_NOT_FOUND.toString(),
                            "No data found"
                        )
                    )
                }

                is AuthorizationException -> {
                    AppLogger.e(
                        TAG,
                        "AUTH RESP=" + exception.message
                    )

                    handleResponseError(
                        errorText = exception.message.safeGet(),
                        callbackWrapper = callbackWrapper
                    )
                }

                is NoContentException -> {
                    callbackWrapper.onEmptyResponse()
                }

                is HTTPBadRequest -> {
                    callbackWrapper.onResponseError(
                        Status(
                            HttpURLConnection.HTTP_BAD_REQUEST.toString(),
                            "Something went wrong , please try again later"
                        )
                    )
                }

                is IOException,
                is TimeoutException -> callbackWrapper.onNetworkError(
                    exception
                )

                else -> callbackWrapper.onServerError(exception)
            }

        })
    }

    private fun handleResponseError(
        errorText: String,
        callbackWrapper: CallbackWrapper<T>
    ) {

        try {
            val baseError = Gson().fromJson(
                errorText,
                BaseError::class.java
            )

            if (baseError != null) {

                baseError.let {

                    if (it.errors.isNullOrEmpty()) {

                        it.error?.let { error ->

                            callbackWrapper.onResponseError(
                                Status(
                                    code = error.code,
                                    message = error.message
                                )
                            )

                        }
                    } else {

                        callbackWrapper.onResponseError(
                            Status(
                                code = it.errors[0].code,
                                message = it.errors[0].message
                            )
                        )
                    }

                }
            } else {
                callbackWrapper.onResponseError(
                    Status(
                        code = "CAN NOT DETERMINE THE ERROR",
                        message = "Something went wrong , please try again later"
                    )
                )
            }
        } catch (e: Exception) {
            //TODO handle here
            e.printStackTrace()
            callbackWrapper.onResponseError(Status(message = "Response error"))
        }
    }
}

class InvalidUseCaseParamsException constructor(
    tag: String,
    message: String = "Invalid arguments in $tag"
) : IOException(message)