package com.pleavinseven.workers

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ResetWorkManagerScheduler(private val context: Context) : WorkManagerScheduler {
    override fun scheduleLogAndReset(habitId: Int, frequency: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTimeMillis = calendar.timeInMillis
        val initialDelayMillis = startTimeMillis - currentTimeMillis
        val habitIdData = Data.Builder()
            .putString("habitId", habitId.toString())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WorkName_$habitId",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<ResetWorker>(frequency, TimeUnit.DAYS)
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .setInputData(habitIdData)
                .build()
        )
    }

    override fun cancel(habitId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("WorkName_$habitId")
    }
}