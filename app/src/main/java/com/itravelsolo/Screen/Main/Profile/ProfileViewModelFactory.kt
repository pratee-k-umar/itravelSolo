package com.itravelsolo.Screen.Main.Profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itravelsolo.data.SessionManager

class ProfileViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(SessionManager(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}