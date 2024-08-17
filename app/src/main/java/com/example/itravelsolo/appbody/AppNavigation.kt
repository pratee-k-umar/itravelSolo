package com.example.itravelsolo.appbody

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itravelsolo.auth.GoogleAuthUiClient
import com.example.itravelsolo.auth.SignInScreen
import com.example.itravelsolo.auth.SignInState
import com.google.android.gms.auth.api.identity.Identity

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavigation(
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient (
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    NavHost(navController = navController, startDestination = Screens.BottomNavigation.Home.route) {
        composable(Screens.BottomNavigation.Home.route) {
            HomeScreen(
                userData = googleAuthUiClient.getSignedInUser(),
            )
        }

        composable("sign_in") {
            SignInScreen(
                state = SignInState(),
                onSignInClick = {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screens.BottomNavigation.Activity.route) {
            Activity()
        }

        composable(Screens.BottomNavigation.Account.route) {
            UserProfile(
//                onSignOut = {
//                    scope.launch {
//                        googleAuthUiClient.signOut()
//                        onSignOut()
//                        navController.navigate("sign_in") {
//                            popUpTo(navController.graph.startDestinationId) {
//                                inclusive = true
//                            }
//                            launchSingleTop = true
//                        }
//                    }
//                }
            )
        }
    }
}