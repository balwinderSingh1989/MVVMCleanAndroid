package com.sample.core.data.remote.service

import com.sample.core.base.BaseUnitTest
import com.sample.core.data.remote.services.login.LoginService
import com.sample.core.data.remote.util.TestUtils.createMockResponse
import com.sample.core.data.repository.login.model.LoginRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class LoginServiceTest : BaseUnitTest() {

    private val infoLoginResponseFile = "login_response.json"

    private lateinit var loginService: LoginService

    private val loginrequest = LoginRequest("343", "1223")


    override fun setUp() {
        super.setUp()
        loginService = retrofit.create(LoginService::class.java)
    }

    @Test
    fun checkLoginServiceResponse() {

        mockWebServer.enqueue(
            createMockResponse(infoLoginResponseFile, HttpURLConnection.HTTP_OK)
        )

        val testObserver = loginService.doLogin(loginrequest).test()

        assertEquals(
            testObserver.values()[0].userId,
            "11"
        )
    }

}