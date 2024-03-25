package com.pleavinseven.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pleavinseven.model.database.Repository

class LogPageViewModelFactory(
    private val repository: Repository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(
                LogPageViewModel::class.java
            )
        ) {

            return LogPageViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}