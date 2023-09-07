package com.pleavinseven

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.model.entities.relations.HabitWithTimeLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val repository: Repository, application: Application
) : AndroidViewModel(application) {

    var count by mutableStateOf(0)
    private var testHabitName = "testHabit"
    var habitWithTimeLogsFlow: Flow<List<HabitWithTimeLogs>> = flowOf()


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
        val habitName = "habit two"
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

    fun getTimeLogs(habitName: String) {
        viewModelScope.launch {
            repository.getHabitWithTimeLogs(habitName).collect { habitWithTimeLogsList ->
                for (timeLog in habitWithTimeLogsList[0].timeLogs) {
                    val formattedTime = formatReadableTime(timeLog)
                }
            }
        }
    }

    private fun formatReadableTime(currentTime: TimeLogModel): String {
        return "${currentTime.day}-${currentTime.month}-${currentTime.year} ${currentTime.hour}:${currentTime.min}"
    }
}