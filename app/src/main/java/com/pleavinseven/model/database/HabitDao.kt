package com.pleavinseven.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.pleavinseven.model.entities.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun addHabit(habit: Habit)

    @Transaction
    @Query("SELECT * FROM habit")
    fun getHabits(): Flow<List<Habit>>

}