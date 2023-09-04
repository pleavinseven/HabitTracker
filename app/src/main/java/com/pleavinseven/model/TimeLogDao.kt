package com.pleavinseven.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface TimeLogDao {

    @Insert
    suspend fun addTimeLog(timeLogModel: TimeLogModel)

    @Delete
    suspend fun deleteTimeLog(timeLogModel: TimeLogModel)

}