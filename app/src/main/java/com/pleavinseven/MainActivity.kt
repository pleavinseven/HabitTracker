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
import com.pleavinseven.viewmodels.MainViewModel
import com.pleavinseven.viewmodels.MainViewModelFactory
import com.pleavinseven.workers.ResetWorkManagerScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        val db = HabitDatabase.getDatabase(application)
        val timeLogDao = db.timeLogDao()
        val habitDao = db.habitDao()
        val repository = Repository(timeLogDao, habitDao)
        val resetWorkManagerScheduler = ResetWorkManagerScheduler(application)
        val viewModel: MainViewModel by viewModels {

            MainViewModelFactory(
                application, repository, resetWorkManagerScheduler
            )
        }
        setContent {
            HabitTrackerTheme {
                Surface {
                    HabitTrackerApp(viewModel)
                }
            }
        }
    }
}