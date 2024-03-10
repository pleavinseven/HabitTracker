package com.pleavinseven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import com.pleavinseven.model.database.HabitDatabase
import com.pleavinseven.model.database.Repository
import com.pleavinseven.ui.HabitTrackerApp
import com.pleavinseven.ui.theme.HabitTrackerTheme
import com.pleavinseven.viewmodels.HabitViewModel
import com.pleavinseven.viewmodels.HabitViewModelFactory
import com.pleavinseven.viewmodels.LazyCalendarViewModel
import com.pleavinseven.viewmodels.TimeLogViewModel
import com.pleavinseven.viewmodels.TimeLogViewModelFactory
import com.pleavinseven.workers.ResetWorkManagerScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        val db = HabitDatabase.getDatabase(application)
        val timeLogDao = db.timeLogDao()
        val habitDao = db.habitDao()
        val dailyCountDao = db.dailyCountDao()
        val repository = Repository(timeLogDao, habitDao, dailyCountDao)
        val resetWorkManagerScheduler = ResetWorkManagerScheduler(application)
        val habitViewModel: HabitViewModel by viewModels {
            HabitViewModelFactory(repository, resetWorkManagerScheduler)
        }
        val timeLogViewModel: TimeLogViewModel by viewModels {
            TimeLogViewModelFactory(repository)
        }
        val lazyCalendarViewModel = LazyCalendarViewModel()
        setContent {
            HabitTrackerTheme {
                Surface {
                    HabitTrackerApp(habitViewModel, timeLogViewModel, lazyCalendarViewModel)
                }
            }
        }
    }
}