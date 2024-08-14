package com.example.itravelsolo.appbody

import androidx.annotation.DrawableRes
import com.example.itravelsolo.R

sealed class Screens(
    val title: String,
    val route: String
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
            R.drawable.baseline_add_home_24
        )
        data object Map: BottomNavigation (
            "Map",
            "map",
            R.drawable.baseline_map_24
        )
    }
}

val screenInNavigation = listOf(
    Screens.BottomNavigation.Home,
    Screens.BottomNavigation.Map
)