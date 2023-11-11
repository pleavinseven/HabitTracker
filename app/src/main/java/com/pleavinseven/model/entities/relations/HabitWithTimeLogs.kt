package com.pleavinseven.model.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel

data class HabitWithTimeLogs(
    @Embedded val habit: Habit,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val timeLogs: List<TimeLogModel>
)