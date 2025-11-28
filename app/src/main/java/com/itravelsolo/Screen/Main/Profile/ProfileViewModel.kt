package com.itravelsolo.Screen.Main.Profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itravelSolo.apollo.MeQuery
import com.itravelsolo.apollo.authToken
import com.itravelsolo.data.AuthRepository
import com.itravelsolo.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading: ProfileState()
    data class Success(
        val user: MeQuery.Me?,
        val profile: MeQuery.Profile?
    ): ProfileState()
    data class Error(
        val message: String
    ): ProfileState()
}

class ProfileViewModel(
    private val sessionManager: SessionManager
): ViewModel() {
    private val repository = AuthRepository()
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    init {
        initializeProfile()
    }

    private fun initializeProfile() {
        viewModelScope.launch {
            sessionManager.authToken.collectLatest { token->
                if(token.isNullOrEmpty()) {
                    _profileState.value = ProfileState.Error("Not logged in..!")
                    return@collectLatest
                }
                authToken = token
                fetchProfileData()
            }
        }
    }

    private suspend fun fetchProfileData() {
        try {
            _profileState.value = ProfileState.Loading
            val response = repository.me()

            if(response.hasErrors()) _profileState.value = ProfileState.Error(response.errors?.first()?.message ?: "Unknown Error")
            else {
                val data = response.data
                _profileState.value = ProfileState.Success(
                    user = data?.me,
                    profile = data?.profile
                )
            }
        }
        catch(e: Exception) {
            _profileState.value = ProfileState.Error(e.message ?: "Unknown Error")
            e.message?.let { Log.e("Profile fetch", it) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            authToken = null
        }
    }
}