package com.pleavinseven.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.model.entities.relations.HabitWithTimeLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeLogDao {

    @Insert
    suspend fun addTimeLog(timeLogModel: TimeLogModel)

    @Delete
    suspend fun deleteTimeLog(timeLogModel: TimeLogModel)

    @Transaction
    @Query("SELECT * FROM habit where id = :habitId")
    fun getHabitWithTimeLogs(habitId: Int): Flow<List<HabitWithTimeLogs>>

}