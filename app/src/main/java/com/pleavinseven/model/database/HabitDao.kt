package com.pleavinseven.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.pleavinseven.model.entities.Habit

@Dao
interface HabitDao {

    @Insert
    suspend fun addHabit(habit: Habit)

}