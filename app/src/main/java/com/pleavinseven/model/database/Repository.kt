package com.pleavinseven.model.database

import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.model.entities.relations.HabitWithTimeLogs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(
    private val timeLogDao: TimeLogDao,
    private val habitDao: HabitDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getHabitWithTimeLogs(habitId: Int): Flow<List<HabitWithTimeLogs>> {
        return timeLogDao.getHabitWithTimeLogs(habitId)
    }

    fun getHabits(): Flow<List<Habit>> {
        return habitDao.getHabits()
    }

    fun getHabitByName(habitName: String): Habit {
        return habitDao.getHabitByName(habitName)
    }

    suspend fun addTimeLog(timeLogModel: TimeLogModel) = withContext(dispatcher) {
        timeLogDao.addTimeLog(timeLogModel)
    }

    suspend fun addHabit(habit: Habit) = withContext(dispatcher) {
        habitDao.addHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) = withContext(dispatcher) {
        habitDao.deleteHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) = withContext(dispatcher) {
        habitDao.updateHabit(habit)
    }

    suspend fun removeLastTimeLog(timeLogModel: TimeLogModel) = withContext(dispatcher) {
        timeLogDao.deleteTimeLog(timeLogModel)
    }
}