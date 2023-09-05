package com.pleavinseven.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.pleavinseven.model.TimeLogModel

@Dao
interface TimeLogDao {

    @Insert
    suspend fun addTimeLog(timeLogModel: TimeLogModel)

    @Delete
    suspend fun deleteTimeLog(timeLogModel: TimeLogModel)

}