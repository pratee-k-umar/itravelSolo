package com.itravelsolo.Screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.itravelsolo.data.SessionManager
import com.itravelsolo.utils.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionManager: SessionManager,
    context: Context
): ViewModel() {
    val isLoggedIn: StateFlow<Boolean?> = sessionManager.authToken.map { token ->
        !token.isNullOrEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _navigateToAuth = MutableSharedFlow<Unit>()
    val navigateToAuth: SharedFlow<Unit> = _navigateToAuth.asSharedFlow()
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _navigateToAuth.emit(Unit)
        }
    }

    private val connectivityObserver = NetworkConnectivityObserver(context)

    val networkStatus = connectivityObserver.observe().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkConnectivityObserver.Status.Unavailable
    )
}

class MainViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(SessionManager(context), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}