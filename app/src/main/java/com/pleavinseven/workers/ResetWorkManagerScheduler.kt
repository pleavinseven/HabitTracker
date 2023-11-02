package com.pleavinseven.workers

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ResetWorkManagerScheduler(private val context: Context) : WorkManagerScheduler {
    override fun scheduleLogAndReset(habitName: String, frequency: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTimeMillis = calendar.timeInMillis
        val initialDelayMillis = startTimeMillis - currentTimeMillis
        val habitNameData = Data.Builder()
            .putString("habitName", habitName)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WorkName_$habitName",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<ResetWorker>(frequency, TimeUnit.DAYS)
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .setInputData(habitNameData)
                .build()
        )
    }
}