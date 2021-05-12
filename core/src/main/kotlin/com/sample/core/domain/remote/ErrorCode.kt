package com.sample.core.domain.remote

import androidx.annotation.StringDef
import com.sample.core.domain.remote.ErrorCode.Companion.EMPTY_RESPONSE
import com.sample.core.domain.remote.ErrorCode.Companion.SESSION_EXPIRE
import com.sample.core.domain.remote.ErrorCode.Companion.UNKNOWN_ERROR

@StringDef(
    EMPTY_RESPONSE,
    SESSION_EXPIRE,
    UNKNOWN_ERROR,
)

annotation class ErrorCode {
    companion object {

        const val EMPTY_RESPONSE = "EMPTY_RESPONSE"
        const val SESSION_EXPIRE = "SESSION_EXPIRE"
        const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
    }
}