package com.pleavinseven.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.R
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

    var habitList: List<Habit> by mutableStateOf(emptyList())

    private val _habitState = MutableStateFlow(Habit(0, "No Habit Selected", 0, null, 1))
    val habitState: StateFlow<Habit> get() = _habitState
    private val _goalColorState = MutableStateFlow(R.color.purple_200)
    val goalColorState: StateFlow<Int> get() = _goalColorState

    private val _showDeleteIcon = MutableStateFlow(false)
    val showDeleteIcon: StateFlow<Boolean> get() = _showDeleteIcon

    private val _deleteHabitList = mutableListOf<Habit>()
    var deleteHabitList: MutableList<Habit> = _deleteHabitList

    init {
        getHabits()
    }

    fun setCurrentHabit(habit: Habit) {
        _habitState.value = habit
    }

    fun setCardColor(habit: Habit): Int {
        val habitColor = if (deleteHabitList.contains(habit)) {
            R.color.delete_red
        } else {
            R.color.white
        }
        return habitColor
    }

    private fun isDeleteListEmpty(){
        if (deleteHabitList.isEmpty()) {
            setShowDelete()
        }
    }

    fun setHabitDeleteList(habit: Habit) {
        isDeleteListEmpty()
        if (deleteHabitList.contains(habit)) {
            _deleteHabitList.remove(habit)
            isDeleteListEmpty()
        } else {
            _deleteHabitList.add(habit)
        }
    }

    fun setShowDelete() {
        _showDeleteIcon.value = !_showDeleteIcon.value
    }

    fun setGoalCompletedColor(habit: Habit) {
        val goal = habit.goal
        val count = habit.count
        if (goal != null) {
            _goalColorState.value =
                if (goal > count) R.color.purple_500 else R.color.goal_reached_color
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
            _habitState.value = updatedHabit
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
        val removedHabits = habitList - habitListFlow.toSet()
        removedHabits.forEach { habit ->
            resetWorkManagerScheduler.cancel(habit.id)
        }
    }

    private fun scheduleResetWorkerForNewHabits(
        habitListFlow: List<Habit>, habitList: List<Habit>
    ) {
        val newHabits = habitListFlow - habitList.toSet()
        newHabits.forEach { habit ->
            resetWorkManagerScheduler.scheduleLogAndReset(habit.id, habit.repeat)
        }
    }

}