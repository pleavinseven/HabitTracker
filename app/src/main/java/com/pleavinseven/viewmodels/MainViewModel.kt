package com.pleavinseven.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
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

    var timeLogsList by mutableStateOf(emptyList<String>())
    var habitList by mutableStateOf(emptyList<Habit>())

    init {
        getHabits()
    }

    fun onCountButtonClicked(habit: Habit) {
        addCount(habit)
        logTimeStampInDatabase(habit.habitName)
    }

    fun onDecreaseButtonClicked(habit: Habit) {
        decreaseCount(habit)
    }

    fun createHabitClicked(habitName: String): Boolean {
        if (!checkHabitDuplicateOrEmpty(habitName)) {
            addHabitToDB(habitName)
            return true
        }
        return false
    }

    private fun addHabitToDB(habitName: String) {
        val habit = Habit(
            habitName, 0
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addHabit(habit)
        }
    }

    private fun checkHabitDuplicateOrEmpty(habitName: String): Boolean {
        if (habitName.isNotBlank()) {
            return habitList.any { habit -> habit.habitName == habitName }
        }
        return true
    }

    private fun addCount(habit: Habit) {
        habit.count++
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCount(habit)
        }
    }

    private fun decreaseCount(habit: Habit) {
        if (habit.count >= 1) {
            habit.count--
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateCount(habit)
            }
        }
    }

    private fun logTimeStampInDatabase(habitName: String) {
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

    private fun getHabits() {
        viewModelScope.launch {
            repository.getHabits().collect { habitListFlow ->
                habitList = habitListFlow
            }
        }
    }

    fun getTimeLogs(habitName: String) {
        viewModelScope.launch {
            repository.getHabitWithTimeLogs(habitName).collect { habitWithTimeLogsList ->
                for (habitWithTimeLog in habitWithTimeLogsList) {
                    timeLogsList = habitWithTimeLog.timeLogs.map { item ->
                        formatReadableTime(item)
                    }
                }
            }
        }
    }

    private fun formatReadableTime(currentTime: TimeLogModel): String {
        return "${currentTime.day}-${currentTime.month}-${currentTime.year} ${currentTime.hour}:${currentTime.min}"
    }

    fun getHabitFromId(habitName: String): Habit {
        val habitFromId = habitList.find { habit ->
            habitName == habit.habitName
        }
        return habitFromId!!
    }
}
