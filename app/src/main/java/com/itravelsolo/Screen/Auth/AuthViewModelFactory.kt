package com.itravelsolo.Screen.Auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itravelsolo.data.SessionManager

class AuthViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T: ViewModel> create (modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(SessionManager(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}