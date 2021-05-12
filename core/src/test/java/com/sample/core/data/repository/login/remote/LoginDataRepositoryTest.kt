package com.sample.core.data.repository.login.remote

import com.nhaarman.mockitokotlin2.whenever
import com.sample.core.data.remote.factory.DataFactory
import com.sample.core.data.repository.DataRemoteConfig
import com.sample.core.data.repository.login.model.LoginRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class LoginDataRepositoryTest {

    private lateinit var loginDataRepository: LoginDataRepository

    private val loginrequest = LoginRequest("11", "1223")

    private val loginResponse =
        DataFactory.generateLoginResponse()

    @Mock
    private lateinit var loginRemote: LoginRemote

    @Mock
    private lateinit var dataRemoteConfig: DataRemoteConfig


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loginDataRepository = LoginDataRepository(loginRemote, dataRemoteConfig)
        stubGetLoginRemoteResponse()

    }

    @Test
    fun stubGetLoginRemoteResponse() {
        GlobalScope.launch {
            whenever(loginRemote.doLogin(loginrequest)).thenReturn(loginResponse)
        }
    }


}