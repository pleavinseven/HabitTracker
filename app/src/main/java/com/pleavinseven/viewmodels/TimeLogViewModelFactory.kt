package com.pleavinseven.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pleavinseven.model.database.Repository

class TimeLogViewModelFactory(
    private val repository: Repository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(
                TimeLogViewModel::class.java
            )
        ) {

            return TimeLogViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}