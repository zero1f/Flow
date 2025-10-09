package com.zero.flow.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zero.flow.presentation.settings.SettingsScreen
import com.zero.flow.presentation.statistics.StatisticsScreen
import com.zero.flow.presentation.tasks.TasksScreen
import com.zero.flow.presentation.timer.TimerScreen



@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ) {
        composable(Screen.Timer.route) {
            TimerScreen(
                onNavigateToTasks = {
                    navController.navigate(Screen.Tasks.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}