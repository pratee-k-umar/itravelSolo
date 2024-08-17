package com.example.itravelsolo.appbody

import androidx.annotation.DrawableRes
import com.example.itravelsolo.R

sealed class Screens(
    val title: String,
    val route: String,
) {
    sealed class UserProfileScreen (
        upsTitle: String,
        upsRoute: String
    ): Screens(upsTitle, upsRoute) {
    }

    sealed class BottomNavigation (
        bnTitle: String,
        bnRoute: String,
        @DrawableRes val icon: Int
    ): Screens(bnTitle, bnRoute) {
        data object Home: BottomNavigation (
            "Home",
            "home",
            R.drawable.baseline_home_filled_24
        )
        data object Activity: BottomNavigation (
            "Activity",
            "activity",
            R.drawable.round_activity_24
        )
        data object Account: BottomNavigation (
            "Account",
            "account",
            R.drawable.baseline_account_circle_24
        )
    }
}

val screenInNavigation = listOf(
    Screens.BottomNavigation.Home,
    Screens.BottomNavigation.Activity,
    Screens.BottomNavigation.Account
)