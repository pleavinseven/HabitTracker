package com.pleavinseven.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pleavinseven.model.database.Repository
import com.pleavinseven.workers.ResetWorkManagerScheduler

class HabitViewModelFactory(
    private val repository: Repository,
    private val resetWorkManagerScheduler: ResetWorkManagerScheduler
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(
                repository, resetWorkManagerScheduler
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}