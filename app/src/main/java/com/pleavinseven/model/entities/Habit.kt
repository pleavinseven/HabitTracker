package com.pleavinseven.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var count: Int,
    var goal: Int?,
    var repeat: Long,
) : Parcelable