package com.pleavinseven.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.workers.ResetWorkManagerScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: Repository,
    private val resetWorkManagerScheduler: ResetWorkManagerScheduler,
) : ViewModel() {

    var habitList by mutableStateOf(emptyList<Habit>())
    private val _mutableHabitState = MutableStateFlow(Habit(0, "No Habit Selected", 0, null, 1))
    val habitState: StateFlow<Habit> = _mutableHabitState
    private val _mutableGoalColorState = MutableStateFlow(Color.Gray)
    val goalColorState: StateFlow<Color> = _mutableGoalColorState

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

    fun onCountButtonClicked(habit: Habit) {
        habit.count++
        updateHabitInDB(habit)
    }

    fun onDecreaseButtonClicked(habit: Habit) {
        if (habit.count >= 1) {
            habit.count--
            updateHabitInDB(habit)
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
        habitListFlow: List<Habit>, habitList: List<Habit>
    ) {
        val removedHabits = habitList - habitListFlow
        removedHabits.forEach { habit ->
            resetWorkManagerScheduler.cancel(habit.id)
        }
    }

    private fun scheduleResetWorkerForNewHabits(
        habitListFlow: List<Habit>, habitList: List<Habit>
    ) {
        val newHabits = habitListFlow - habitList
        newHabits.forEach { habit ->
            resetWorkManagerScheduler.scheduleLogAndReset(habit.id, habit.repeat)
        }
    }

}