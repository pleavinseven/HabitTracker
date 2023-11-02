package com.pleavinseven.workers

interface WorkManagerScheduler {
    fun scheduleLogAndReset(habitName: String, frequency: Long)
}