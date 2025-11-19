package com.itravelsolo.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.itravelsolo.data.AuthRepository

sealed class AuthResult {
    object Idle: AuthResult()
    object Loading: AuthResult()
    data class Success(val message: String?): AuthResult()
    data class Error(val message: String?): AuthResult()
}

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()
    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)

    val authResult: StateFlow<AuthResult> = _authResult

    fun signUpUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val response = repository.signUpUser(name, email, password)
                val responseData = response.data?.registerUser
                if(response.hasErrors() || responseData == null) {
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                    _authResult.value = AuthResult.Error(errorMessage)
                }
                else responseData.success?.let {
                    if(!it) _authResult.value = AuthResult.Error(responseData.message)
                    else _authResult.value = AuthResult.Success(responseData.message)
                }
            }
            catch(e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "A network error occurred")

            }
        }
    }
}