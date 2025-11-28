package com.itravelsolo.Screen.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itravelsolo.Screen.Main.Profile.Profile
import com.itravelsolo.Screen.MainViewModel
import com.itravelsolo.ui.components.AppBottomBar
import com.itravelsolo.ui.components.BottomNavItem

@Composable
fun MainAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BottomNavItem.Home.route,
    ) {
        composable(BottomNavItem.Home.route) {
            Home(
                navController = navController
            )
        }
        composable(BottomNavItem.Profile.route) {
            Profile(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
fun MainApp(
    mainViewModel: MainViewModel,
    isOffline: Boolean
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.background(Color.Black),
        bottomBar = {
            AppBottomBar(navController = navController)
        },
    ) { innerPadding ->
        if(isOffline) NoInternetScreen()
        else MainAppNavigation(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            mainViewModel = mainViewModel
        )
    }
}

@Composable
fun NoInternetScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Internet Connection",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Text(
                text = "Please check your connection and try again.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}