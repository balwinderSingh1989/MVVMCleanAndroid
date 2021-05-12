package com.sample.core.domain

import com.sample.core.domain.kotlinflow.Resource
import com.sample.core.domain.remote.ErrorUtil
import com.sample.core.utility.extensions.safeGet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUsecase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    fun execute(parameters: P? = null): Flow<Resource<R>> {
        return buildUsecase(parameters)
            .buffer()
            .catch { e ->
                val errorStatus = ErrorUtil.getErrorStatus(e)
                emit(
                    Resource.Error(
                        throwable = Throwable(e),
                        errorCode = errorStatus.code.safeGet(),
                        errorDescription = errorStatus.message
                    )
                )
            }
            .flowOn(coroutineDispatcher)
    }

    abstract fun buildUsecase(parameters: P?): Flow<Resource<R>>

}