package com.edu.auth.presentation.login

import android.R.attr.password
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.auth.domain.AuthRepository
import com.edu.auth.domain.UserDataValidator
import com.edu.auth.presentation.R
import com.edu.auth.presentation.util.textAsFlow
import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.Result
import com.edu.core.presentation.ui.UiText
import com.edu.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
): ViewModel() {
    var state by mutableStateOf(LoginState())
        private set
    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    init{
        combine(state.emailState.textAsFlow(),state.passwordState.textAsFlow()){ email,password ->
            state = state.copy(
                canLogin = userDataValidator.isValidEmail(
                    email =email.toString().trim()
                ) && password.isNotEmpty()
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: LoginAction){
        when(action){
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
            else -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {

            state = state.copy(
                isLoggingIn = true
            )
            val result = authRepository.login(
                email =state.emailState.text.toString().trim(),
                password = state.passwordState.text.toString()
            )
            state = state.copy(
                isLoggingIn = false
            )
            when(result){
                is Result.Error ->{
                    if(result.error == DataError.Network.UNAUTHORIZED){
                        _events.send(LoginEvent.Error(
                            UiText.StringResource(
                                id= R.string.error_email_password_incorrect)
                        )
                        )

                    }else{
                        _events.send(LoginEvent.Error(result.error.asUiText()))
                    }
                }
                is Result.Success -> {
                    _events.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }


}