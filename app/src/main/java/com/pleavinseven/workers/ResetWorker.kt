package com.pleavinseven.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pleavinseven.model.database.HabitDatabase
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.DailyCount
import com.pleavinseven.model.entities.Habit
import java.time.LocalDateTime

class ResetWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    private val db = HabitDatabase.getDatabase(applicationContext)
    private val timeLogDao = db.timeLogDao()
    private val habitDao = db.habitDao()
    private val dailyCountDao = db.dailyCountDao()
    private val repository = Repository(timeLogDao, habitDao, dailyCountDao)
    private val habitId = inputData.getString("habitId")?.toInt()
    val habit: Habit = repository.getHabitById(habitId!!)

    override suspend fun doWork(): Result {
        return try {
            logDailyCount()
            resetHabits()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun logDailyCount() {
        val currentTime = LocalDateTime.now()
        val count = habit.count
        val year = currentTime.year
        val month = currentTime.monthValue
        val day = currentTime.dayOfMonth
        val dailyCount = DailyCount(0, year, month, day, count, habitId!!)
        repository.setDailyCount(dailyCount)
    }

    private suspend fun resetHabits() {
        habit.count = 0
        repository.updateHabit(habit)
    }
}