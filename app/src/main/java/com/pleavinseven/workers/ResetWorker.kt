package com.pleavinseven.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pleavinseven.model.database.HabitDatabase
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit

class ResetWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    private val db = HabitDatabase.getDatabase(applicationContext)
    private val timeLogDao = db.timeLogDao()
    private val habitDao = db.habitDao()
    private val repository = Repository(timeLogDao, habitDao)
    private val habitName = inputData.getString("habitName")
    val habit: Habit = repository.getHabitByName(habitName!!)
    override suspend fun doWork(): Result {
        return try {
            logDailyCount()
            resetHabits()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun logDailyCount() {

    }

    private suspend fun resetHabits() {
        habit.count = 0
        repository.updateHabit(habit)
    }


}