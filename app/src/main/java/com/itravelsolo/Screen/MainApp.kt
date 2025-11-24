package com.itravelsolo.Screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itravelsolo.Screen.Main.Home
import com.itravelsolo.Screen.Main.Profile
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
fun MainApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    ) { innerPadding ->
        MainAppNavigation(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            mainViewModel = mainViewModel
        )
    }
}