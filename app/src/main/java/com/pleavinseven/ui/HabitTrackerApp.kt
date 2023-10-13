package com.pleavinseven.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pleavinseven.viewmodels.MainViewModel

@Composable
fun HabitTrackerApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "HabitsPage") {
        composable("HabitsPage") {
            HabitsPage(viewModel = viewModel, navController)
        }
        composable("CounterPage/{habitId}") { backStackEntry ->
            backStackEntry.arguments?.getString("habitId")
                ?.let { CounterPage(viewModel = viewModel, it.toInt()) }
        }
    }
}