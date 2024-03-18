package com.pleavinseven.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.model.entities.relations.HabitWithTimeLogs

@Dao
interface TimeLogDao {

    @Insert
    suspend fun addTimeLog(timeLogModel: TimeLogModel)

    @Delete
    suspend fun deleteTimeLog(timeLogModel: TimeLogModel)

    @Transaction
    @Query("SELECT * FROM habit" +
            " INNER JOIN daily_count ON habit.id = daily_count.habitId" +
            " INNER JOIN time_log ON habit.id = time_log.habitId" +
            " WHERE daily_count.year = :year AND daily_count.month = :month")
    suspend fun getMonthlyHabitDataList( month: Int, year: Int): List<HabitWithTimeLogs>

}