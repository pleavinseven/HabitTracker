package com.pleavinseven.model.database

import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.model.entities.relations.HabitWithTimeLogs
import kotlinx.coroutines.flow.Flow

class Repository(private val timeLogDao: TimeLogDao, private val habitDao: HabitDao) {

    fun getHabitWithTimeLogs(habitName: String): Flow<List<HabitWithTimeLogs>> {
        return timeLogDao.getHabitWithTimeLogs(habitName)
    }

    suspend fun addTimeLog(timeLogModel: TimeLogModel) {
        timeLogDao.addTimeLog(timeLogModel)
    }

    suspend fun addHabit(habit: Habit) {
        habitDao.addHabit(habit)
    }

    suspend fun deleteTimeLog(timeLogModel: TimeLogModel) {
        timeLogDao.deleteTimeLog(timeLogModel)
    }

}