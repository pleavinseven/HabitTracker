package com.pleavinseven.ui

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.pleavinseven.composables.MyTheme
import com.pleavinseven.viewmodels.HabitViewModel
import com.pleavinseven.viewmodels.LazyCalendarViewModel
import com.pleavinseven.viewmodels.TimeLogViewModel

@Composable
fun LogPage(
    navController: NavController,
    timeLogViewModel: TimeLogViewModel,
    lazyCalendarViewModel: LazyCalendarViewModel,
    habitViewModel: HabitViewModel
) {
    val size = timeLogViewModel.timeLogList
    MyTheme(navController = navController) {
        LazyCalendar(lazyCalendarViewModel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ){
            //graphs n shit
        }
    }
}