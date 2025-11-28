package com.itravelsolo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.itravelsolo.Screen.Auth.Auth
import com.itravelsolo.Screen.Main.MainApp
import com.itravelsolo.Screen.MainViewModel
import com.itravelsolo.Screen.MainViewModelFactory
import com.itravelsolo.Screen.Loading.OnBoard
import com.itravelsolo.Screen.Loading.Splash
import com.itravelsolo.ui.theme.ItravelSoloTheme
import com.itravelsolo.utils.NetworkConnectivityObserver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()))
        setContent {
            ItravelSoloTheme {
                val context = LocalContext.current
                val mainViewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(context)
                )
                val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
                val networkStatus by mainViewModel.networkStatus.collectAsState()
                splashscreen.setKeepOnScreenCondition {
                    isLoggedIn == null
                }
                if(isLoggedIn != null) {
                    val isOffline = networkStatus == NetworkConnectivityObserver.Status.Unavailable || networkStatus == NetworkConnectivityObserver.Status.Lost
                    Navigation(
                        isLoggedIn = isLoggedIn!!,
                        mainViewModel = mainViewModel,
                        isOffline = isOffline
                    )
                }
            }
        }
    }
}

@Composable
fun Navigation(
    isLoggedIn: Boolean,
    mainViewModel: MainViewModel,
    isOffline: Boolean
) {
    val navController = rememberNavController()
    var showOnboarding by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        mainViewModel.navigateToAuth.collect {
            navController.navigate("auth") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) "main" else "splash"
    ) {
        composable("splash") {
            val destination = if(showOnboarding) "onBoarding" else "auth"
            Splash(onTimeout = {
                navController.navigate(destination) {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            })
        }
        composable("onboarding") {
            OnBoard(
                onFinished = {
                    navController.navigate("auth") {
                        popUpTo("onboarding") {
                            inclusive = true
                        }
                    }
                    showOnboarding = false
                }
            )
        }
        composable(
            "auth",
            deepLinks = listOf(
                navDeepLink { uriPattern = "android-app://androidx.navigation/auth" }
            )
        ) {
            Auth(navController)
        }
        composable("main") {
            MainApp(
                mainViewModel = mainViewModel,
                 isOffline = isOffline
            )
        }
    }
}