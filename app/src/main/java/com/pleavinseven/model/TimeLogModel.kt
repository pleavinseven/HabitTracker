package com.pleavinseven.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class TimeLogModel(
    @PrimaryKey(autoGenerate = true)
    val logId: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val min: Int,
    val seconds: Int?,
    )