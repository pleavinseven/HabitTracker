package com.pleavinseven.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
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
    ): Parcelable