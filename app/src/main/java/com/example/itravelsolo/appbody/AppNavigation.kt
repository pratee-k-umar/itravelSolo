package com.example.itravelsolo.appbody

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
import kotlinx.coroutines.launch

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
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
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

        composable("profile_screen") {
//            ProfileScreen(
//                userData = googleAuthUiClient.getSignedInUser()
//            )
        }
    }
}

//onSignOut = {
//    scope.launch {
//        googleAuthUiClient.signOut()
//        onSignOut()
//        navController.navigate("sign_in") {
//            popUpTo(navController.graph.startDestinationId) {
//                inclusive = true
//            }
//            launchSingleTop = true
//        }
//    }
//}