package com.example.itravelsolo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.itravelsolo.appbody.Screens

class MainViewModel: ViewModel() {
    private val _currentScreen: MutableState<Screens> = mutableStateOf(Screens.BottomNavigation.Home)
    val currentScreen: MutableState<Screens> get() = _currentScreen
    fun setCurrentScreen(screen: Screens) {
        _currentScreen.value = screen
    }
}