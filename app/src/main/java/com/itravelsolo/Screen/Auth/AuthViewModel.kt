package com.itravelsolo.Screen.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.itravelsolo.data.AuthRepository
import com.itravelsolo.data.SessionManager

sealed class AuthResult {
    object Idle: AuthResult()
    object Loading: AuthResult()
    object OTPSent: AuthResult()
    data class GeneralSuccess(val message: String?): AuthResult()
    data class AuthenticationSuccess(val message: String?): AuthResult()
    data class Error(val message: String?): AuthResult()
}

enum class VerificationType {
    SignUpVerification,
    MFAEnable,
    MFADisable
}

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val repository = AuthRepository()
    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)

    val authResult: StateFlow<AuthResult> = _authResult

    var currentEmail: String = ""
    private set

    fun signUpUser(name: String, email: String, password: String) {
        currentEmail = email
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val response = repository.signUpUser(name, email, password)
                val data = response.data?.registerUser
                if(data?.success == true) {
                    val user = data.user
                    if(user != null) {
                        sessionManager.saveUserId(user.id as String)
                        _authResult.value = AuthResult.OTPSent
                    }
                    else _authResult.value = AuthResult.Error("Invalid response data")
                }
                else _authResult.value = AuthResult.Error(data?.message ?: "Sign up failed..!")
            }
            catch(e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "A network error occurred")

            }
        }
    }

    fun signInUser(email: String, password: String) {
        currentEmail = email
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val response = repository.signInUser(email, password)
                val data = response.data?.loginUser

                if(response.hasErrors() || data == null) _authResult.value = AuthResult.Error(response.errors?.firstOrNull()?.message ?: "Unknown error")
                else if(data.success != true) _authResult.value = AuthResult.Error(data.message ?: "Login Failed..!")
                else {
                    val user = data.user
                    val token = data.token
                    val refreshToken = data.refreshToken
                    if(user != null && token != null && refreshToken != null) {
                        sessionManager.saveUserSession(user.id as String, token, refreshToken)
                        _authResult.value = AuthResult.AuthenticationSuccess(data.message)
                    }
                    else _authResult.value = AuthResult.Error("Incomplete login data received..!")
                }
            }
            catch(e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "A network error occurred")
            }
        }
    }

    fun resetResult() {
        _authResult.value = AuthResult.Idle
    }

    fun requestEmailVerificationOtp() {
        if(currentEmail.isEmpty()) {
            _authResult.value = AuthResult.Error("Invalid email")
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.requestEmailVerificationOtp(currentEmail)
                val data = response.data?.requestEmailVerificationOtp

                if(data?.success == true) _authResult.value = AuthResult.GeneralSuccess("OTP sent successfully...")
                else _authResult.value = AuthResult.Error(data?.message ?: "OTP request failed..!")
            }
            catch(e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "A network error occurred")
            }
        }
    }

    fun verifyOTP(otp: String, type: VerificationType) {
        if(currentEmail.isEmpty()) {
            _authResult.value = AuthResult.Error("Invalid email")
            return
        }

        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val response = repository.verifyEmailOTP(currentEmail, otp)
                val data = response.data?.verifyEmailOtp

                if (data?.success == true) {
                    when(type) {
                        VerificationType.SignUpVerification -> {
                            val token = data.token
                            val refreshToken = data.refreshToken

                            if (token != null && refreshToken != null) {
                                sessionManager.saveAuthTokens(token, refreshToken)
                                _authResult.value = AuthResult.AuthenticationSuccess(data.message)
                            }
                            else _authResult.value = AuthResult.Error("Invalid response data")
                        }
                        VerificationType.MFAEnable, VerificationType.MFADisable -> {
                            _authResult.value = AuthResult.GeneralSuccess(data.message)
                        }
                    }
                }
                else _authResult.value = AuthResult.Error(data?.message ?: "Verification failed..!")
            }
            catch(e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "A network error occurred")
            }
        }
    }
}