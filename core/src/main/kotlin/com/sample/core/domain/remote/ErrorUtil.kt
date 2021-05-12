package com.sample.core.domain.remote

import com.google.gson.Gson
import com.sample.core.domain.remote.ErrorCode.Companion.UNKNOWN_ERROR
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.extensions.safeGet
import com.sample.core.utility.logger.AppLogger
import retrofit2.HttpException
import java.io.PrintWriter
import java.io.StringWriter
import java.net.HttpURLConnection.*
import java.util.concurrent.TimeoutException

object ErrorUtil {


    fun getErrorStatus(e: Throwable): Status =
        if (e is HttpException) {
            getResponseStatus(e.response()?.errorBody()?.string().safeGet())
        } else {
            //print error stacktrace for debugging purpose
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))
            AppLogger.e(TAG, stringWriter.toString())
            Status(UNKNOWN_ERROR, e.localizedMessage.safeGet())
        }

    private fun getResponseStatus(errorText: String): Status {
        try {
            val baseError = Gson().fromJson(
                errorText,
                BaseError::class.java
            )
            if (baseError != null) {
                baseError.let {
                    if (it.errors.isNullOrEmpty()) {
                        it.error?.let { error ->
                            return Status(
                                code = error.code,
                                message = error.message
                            )
                        }
                    } else {
                        return Status(
                            code = it.errors[0].code,
                            message = it.errors[0].message
                        )
                    }
                }
            } else {
                return Status(
                    code = "CAN NOT DETERMINE THE ERROR",
                    message = "Something went wrong , please try again later"
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, throwable = e, message = e.message.safeGet())
            return Status(message = "Response error")
        }
        return Status(UNKNOWN_ERROR, UNKNOWN_ERROR)
    }

}