package com.pleavinseven.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TimeLogViewModel(private val repository: Repository) : ViewModel() {
    var timeLogList by mutableStateOf(emptyList<TimeLogModel>())

    fun removeLastTimeLog(habit: Habit) {
        if(habit.count > 0) {
            viewModelScope.launch {
                repository.removeLastTimeLog(timeLogList.last())
            }
        }
    }

    fun logTimeStampInDatabase(habitId: Int) {
        val currentTime = LocalDateTime.now()
        val timeLogModel = TimeLogModel(
            logId = 0,
            year = currentTime.year,
            month = currentTime.monthValue,
            day = currentTime.dayOfMonth,
            hour = currentTime.hour,
            min = currentTime.minute,
            seconds = currentTime.second,
            habitId = habitId
        )
        viewModelScope.launch {
            repository.addTimeLog(timeLogModel)
        }
    }
}