package com.pleavinseven.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.workers.ResetWorkManagerScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val repository: Repository,
    private val resetWorkManagerScheduler: ResetWorkManagerScheduler,
    application: Application,
) : AndroidViewModel(application) {

    var formattedTimeLogList by mutableStateOf(emptyList<String>())
    var timeLogList by mutableStateOf(emptyList<TimeLogModel>())
    var habitList by mutableStateOf(emptyList<Habit>())
    private val _mutableHabitState = MutableStateFlow(Habit(0, "No Habit Selected", 0, null, 1))
    val habitState: StateFlow<Habit> = _mutableHabitState
    private val _mutableGoalColorState = MutableStateFlow(Color.Gray)
    val goalColorState: StateFlow<Color> = _mutableGoalColorState
    private val _mutableNavBarPosition = MutableStateFlow(0)
    val navBarPosition: StateFlow<Int> = _mutableNavBarPosition

    init {
        getHabits()
    }

    fun setCurrentHabit(habit: Habit) {
        (habitState as MutableStateFlow).value = habit
    }

    fun setGoalColor(habit: Habit, grey: Color, green: Color) {
        val goal = habit.goal
        val count = habit.count
        if (goal != null) {
            _mutableGoalColorState.value = if (goal > count) grey else green
        }
    }

    fun setNavBarPosition(pos: Int) {
        _mutableNavBarPosition.value = pos
    }

    fun onCountButtonClicked(habit: Habit) {
        addCount(habit)
        logTimeStampInDatabase(habit.id)
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
            return true
        }
        return false
    }

    fun updateHabitClicked(habit: Habit, habitName: String, habitGoal: Int?): Boolean {
        // if new name is allowed update and return true else return false
        val habitId = habit.id
        if (!isHabitDuplicateOrEmpty(habitName, habit)) {
            resetWorkManagerScheduler.cancel(habit.id)
            val updatedHabit = habit.copy(name = habitName, goal = habitGoal)
            updateHabitInDB(updatedHabit)
            _mutableHabitState.value = updatedHabit
            resetWorkManagerScheduler.scheduleLogAndReset(habitId, 1)
            return true
        }
        return false
    }

    fun onHabitConfirmDeleteClick(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun getTimeLogs(habitId: Int) {
        viewModelScope.launch {
            repository.getHabitWithTimeLogs(habitId).collect { habitWithTimeLogsList ->
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


    private fun addHabitToDB(habitName: String, habitGoal: Int?, habitRepeat: Long) {
        val habit = Habit(
            0, habitName, 0, habitGoal, habitRepeat
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

    private fun logTimeStampInDatabase(habitId: Int) {
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

    private fun getHabits() {
        viewModelScope.launch {
            repository.getHabits().distinctUntilChanged().collect { habitListFlow ->
                scheduleResetWorkerForNewHabits(habitListFlow, habitList)
                cancelResetWorkerForRemovedHabits(habitListFlow, habitList)
                habitList = habitListFlow
            }
        }
    }

    private fun cancelResetWorkerForRemovedHabits(
        habitListFlow: List<Habit>,
        habitList: List<Habit>
    ) {
        val removedHabits = habitList - habitListFlow
        removedHabits.forEach { habit ->
            resetWorkManagerScheduler.cancel(habit.id)
        }
    }

    private fun scheduleResetWorkerForNewHabits(
        habitListFlow: List<Habit>,
        habitList: List<Habit>
    ) {
        val newHabits = habitListFlow - habitList
        newHabits.forEach { habit ->
            resetWorkManagerScheduler.scheduleLogAndReset(habit.id, habit.repeat)
        }
    }

    private fun formatReadableTime(currentTime: TimeLogModel): String {
        return "${currentTime.day}-${currentTime.month}-${currentTime.year} ${currentTime.hour}:${currentTime.min}"
    }
}
