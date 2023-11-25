package com.pleavinseven.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.pleavinseven.model.entities.DailyCount

@Dao
interface  DailyCountDao {

    @Insert
    suspend fun setDailyCount(dailyCount: DailyCount)
}