package com.pleavinseven.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pleavinseven.viewmodels.HabitViewModel
import com.pleavinseven.viewmodels.NavigationViewModel
import com.pleavinseven.viewmodels.TimeLogViewModel

@Composable
fun HabitTrackerApp(
    navigationViewModel: NavigationViewModel,
    habitViewModel: HabitViewModel,
    timeLogViewModel: TimeLogViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "HabitsPage") {
        composable("HabitsPage") {
            HabitsPage(navigationViewModel, habitViewModel, timeLogViewModel, navController)
        }
        composable("CounterPage") {
            CounterPage(navigationViewModel, habitViewModel, timeLogViewModel, navController)
        }
        composable("LogPage") {
            LogPage(navigationViewModel, navController)
        }
        composable("Settings") {
            Settings(navigationViewModel, navController)
        }
    }
}