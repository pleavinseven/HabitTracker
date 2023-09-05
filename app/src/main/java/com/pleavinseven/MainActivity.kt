package com.pleavinseven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.pleavinseven.model.database.HabitDatabase
import com.pleavinseven.model.database.Repository
import com.pleavinseven.ui.HabitTrackerApp
import com.pleavinseven.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = HabitDatabase.getDatabase(application)
        val timeLogDao = db.timeLogDao()
        val repository = Repository(timeLogDao)

        val viewModel: MainViewModel by viewModels {

            MainViewModelFactory(
                application, repository
            )
        }

        setContent {
            HabitTrackerTheme {
                HabitTrackerApp(viewModel)
            }
        }
    }
}