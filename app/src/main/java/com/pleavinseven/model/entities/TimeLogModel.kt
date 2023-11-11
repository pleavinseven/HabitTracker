package com.pleavinseven.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "time_log")
data class TimeLogModel(
    @PrimaryKey(autoGenerate = true) val logId: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val min: Int,
    val seconds: Int?,
    val habitId: Int
) : Parcelable