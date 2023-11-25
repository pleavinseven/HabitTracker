package com.pleavinseven.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "daily_count")
data class DailyCount(
    @PrimaryKey(autoGenerate = true) var countId: Int,
    var year: Int,
    var month: Int,
    var day: Int,
    var count: Int,
    var habitId: Int,
) : Parcelable
