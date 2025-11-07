package com.itravelsolo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itravelsolo.Screen.Auth
import com.itravelsolo.Screen.Home
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isLoading by viewModel.isLoading.collectAsState()
                    if(!isLoading) Navigation()
                }
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            Splash(onTimeout = {
                navController.navigate("auth") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            })
        }
        composable("auth") {
            Auth(navController)
        }
        composable("home") {
            Home(navController)
        }
    }
}