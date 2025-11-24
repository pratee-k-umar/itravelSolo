package com.itravelsolo.Screen.Main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.itravelsolo.Screen.MainViewModel

@Composable
fun Profile(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile")
        Button(onClick = {
            mainViewModel.logout()
        }) {
            Text("Logout")
        }
    }
}