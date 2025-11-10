package com.itravelsolo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.itravelsolo.Screen.Auth
import com.itravelsolo.Screen.Auth.OTPVerify
import com.itravelsolo.Screen.Main.Home
import com.itravelsolo.Screen.Main.Profile
import com.itravelsolo.Screen.OnBoard
import com.itravelsolo.Screen.Splash
import com.itravelsolo.ui.theme.ItravelSoloTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        splashscreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItravelSoloTheme {
                val isLoading by viewModel.isLoading.collectAsState()
                if(!isLoading) Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    var showOnboarding by remember { mutableStateOf(true) }
    NavHost(
        navController = navController,
        startDestination = "splash"
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
        navigation("auth", "auth_flow") {
            composable("auth") {
                Auth(navController)
            }
            composable("otp") {
                OTPVerify()
            }
        }
        navigation("home", "main_flow") {
            composable("home") {
                Home(navController)
            }
            composable("profile") {
                Profile(navController)
            }
        }
    }
}