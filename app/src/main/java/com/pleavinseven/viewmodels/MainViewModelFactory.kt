package com.pleavinseven.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pleavinseven.model.database.Repository
import com.pleavinseven.workers.ResetWorkManagerScheduler

class MainViewModelFactory(

    private val application: Application,
    private val repository: Repository,
    private val resetWorkManagerScheduler: ResetWorkManagerScheduler
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(
                MainViewModel::class.java
            )
        ) {

            return MainViewModel(repository, resetWorkManagerScheduler, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}