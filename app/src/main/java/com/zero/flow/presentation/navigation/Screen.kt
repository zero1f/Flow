package com.zero.flow.presentation.navigation

sealed class Screen(val route: String) {
    data object Timer : Screen("timer")
    data object Tasks : Screen("tasks")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
}