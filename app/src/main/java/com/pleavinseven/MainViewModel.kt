package com.pleavinseven

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val repository: Repository, application: Application
) : AndroidViewModel(application) {

    var count by mutableStateOf(0)
    private var testHabitName = "testHabit"
    var timeLogsList = mutableStateListOf<String>()
    var habitList = mutableStateListOf<Habit>()


    fun onCountButtonClicked() {
        addCount()
        logTimeStampInDatabase()
        getTimeLogs(testHabitName)
    }

    private fun addHabitToDB(habitName: String) {
        val habit = Habit(
            habitName, count
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addHabit(habit)
        }
    }

    private fun addCount() {
        count++
    }

    private fun logTimeStampInDatabase() {
        val habitName = testHabitName
        val currentTime = LocalDateTime.now()
        val timeLogModel = TimeLogModel(
            logId = 0,
            year = currentTime.year,
            month = currentTime.monthValue,
            day = currentTime.dayOfMonth,
            hour = currentTime.hour,
            min = currentTime.minute,
            seconds = currentTime.second,
            habitName = habitName
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTimeLog(timeLogModel)
        }
    }

    private fun getTimeLogs(habitName: String) {
        viewModelScope.launch {
            repository.getHabitWithTimeLogs(habitName).collect { habitWithTimeLogsList ->
                for (habitWithTimeLog in habitWithTimeLogsList) {
                    val _timeLogsList = habitWithTimeLog.timeLogs

                    for (item in _timeLogsList) {
                        val formatted = formatReadableTime(item)
                        timeLogsList += formatted
                    }
                }
            }
        }
    }

    private fun formatReadableTime(currentTime: TimeLogModel): String {
        return "${currentTime.day}-${currentTime.month}-${currentTime.year} ${currentTime.hour}:${currentTime.min}"
    }
}