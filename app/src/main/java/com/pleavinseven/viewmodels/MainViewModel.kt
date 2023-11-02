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
import com.pleavinseven.workers.ResetWorkManagerScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val repository: Repository,
    application: Application,
) : AndroidViewModel(application) {

    var formattedTimeLogList by mutableStateOf(emptyList<String>())
    var timeLogList by mutableStateOf(emptyList<TimeLogModel>())
    var habitList by mutableStateOf(emptyList<Habit>())
    val habitState: StateFlow<Habit> = MutableStateFlow(Habit(0, "No Habit Selected", 0, null, 1))
    private val resetWorkManagerScheduler: ResetWorkManagerScheduler = ResetWorkManagerScheduler(application)

    init {
        getHabits()
    }

    fun setCurrentHabit(habit: Habit) {
        (habitState as MutableStateFlow).value = habit
    }

    fun onCountButtonClicked(habit: Habit) {
        addCount(habit)
        logTimeStampInDatabase(habit.name)
    }

    fun onDecreaseButtonClicked(habit: Habit) {
        if (habit.count >= 1) {
            decreaseCount(habit)
            viewModelScope.launch {
                repository.removeLastTimeLog(timeLogList.last())
            }
        }
    }

    fun createHabitClicked(habitName: String, habitGoal: Int?, habitRepeat: Long): Boolean {
        if (!isHabitDuplicateOrEmpty(habitName)) {
            addHabitToDB(habitName, habitGoal, habitRepeat)
            resetWorkManagerScheduler.scheduleLogAndReset(habitName, habitRepeat)
            return true
        }
        return false
    }

    fun updateHabitClicked(
        // if new name is allowed update and return true else return false
        habit: Habit, habitName: String, habitGoal: Int?): Boolean {
        if (!isHabitDuplicateOrEmpty(habitName, habit)) {
            habit.name = habitName
            habit.goal = habitGoal
            updateHabitInDB(habit)
            return true
        }
        return false
    }

    fun onHabitConfirmDeleteClick(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun getTimeLogs(habitName: String) {
        viewModelScope.launch {
            repository.getHabitWithTimeLogs(habitName).collect { habitWithTimeLogsList ->
                for (habitWithTimeLog in habitWithTimeLogsList) {
                    timeLogList = habitWithTimeLog.timeLogs
                    formattedTimeLogList = habitWithTimeLog.timeLogs.map { item ->
                        formatReadableTime(item)
                    }
                }
            }
        }
    }

    private fun updateHabitInDB(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }


    private fun addHabitToDB(habitName: String, habitGoal: Int?) {
        val habit = Habit(
            0, habitName, 0, habitGoal
        )
        viewModelScope.launch {
            repository.addHabit(habit)
        }
    }

    private fun isHabitDuplicateOrEmpty(habitName: String, habit: Habit? = null): Boolean {
        if (habit?.name == habitName) {
            return false
        }
        if (habitName.isNotBlank()) {
            return habitList.any { currentHabit -> currentHabit.name == habitName }
        }
        return true
    }

    private fun addCount(habit: Habit) {
        habit.count++
        updateHabitInDB(habit)
    }

    private fun decreaseCount(habit: Habit) {
        habit.count--
        updateHabitInDB(habit)
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
        viewModelScope.launch {
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

    private fun formatReadableTime(currentTime: TimeLogModel): String {
        return "${currentTime.day}-${currentTime.month}-${currentTime.year} ${currentTime.hour}:${currentTime.min}"
    }

}
