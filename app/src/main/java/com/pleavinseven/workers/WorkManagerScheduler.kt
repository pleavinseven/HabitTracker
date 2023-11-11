package com.pleavinseven.workers

interface WorkManagerScheduler {
    fun scheduleLogAndReset(habitId: Int, frequency: Long)

    fun cancel(habitId: Int)
}