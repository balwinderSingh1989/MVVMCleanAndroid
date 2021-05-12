package com.sample.assignment.ui.login

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sample.assignment.base.BaseViewModel
import com.sample.core.data.cache.AppData
import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.domain.kotlinflow.Resource
import com.sample.core.domain.usecase.LoginUseCase
import com.sample.core.utility.event.SingleLiveEvent
import com.sample.core.utility.extensions.isEmailValid
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    private val loginusecase: LoginUseCase,
    private val appData: AppData
) : BaseViewModel() {
    companion object {
        const val TAG = "LoginViewModel"
    }

    val loginSuccessEvent = SingleLiveEvent<Void>()
    val loginFailEvent = SingleLiveEvent<String>()

    private var _loginClicked = SingleLiveEvent<Void>()
    val loginClicked: LiveData<Void>
        get() = _loginClicked


    var errorEmail = ObservableField<String>()
    var errorPassword = ObservableField<String>()

    fun loginClicked() {
        _loginClicked.call()
    }


    fun login(userName: String, passWord: String) {
        var isValidated = true
        if (userName.isNullOrEmpty() ||  !userName.isEmailValid()) {
            errorEmail.set("Please enter valid email here")
            isValidated = false
        }
        if (passWord.isNullOrEmpty()) {
            errorPassword.set("Please enter password")
            isValidated = false
        }

        if(isValidated) {
            viewModelScope.launch {
                showLoading("Logging you in...")
                loginusecase.execute(
                    LoginUseCase.Params.create(
                        loginrequest = LoginRequest(
                            email = userName,
                            password = passWord
                        )
                    )
                ).collect { loadResult ->
                    when (loadResult) {
                        is Resource.Success -> {
                            hideLoading()
                            appData.saveUserID(loadResult.data.userId)
                            appData.savePassword(passWord)
                            appData.saveEmail(userName)
                            mSessionManager.setToken(loadResult.data.token)
                            loginSuccessEvent.call()
                        }
                        is Resource.Error -> {
                            hideLoading()
                            onLoginFailed(loadResult.errorDescription)
                        }


                    }
                }
            }
        }

    }

    private fun onLoginFailed(failMessage: String) {
        hideLoading()
        loginFailEvent.apply {
            value = failMessage
        }

    }


}