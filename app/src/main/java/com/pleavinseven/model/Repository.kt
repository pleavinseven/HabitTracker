package com.pleavinseven.model


class Repository(private val timeLogDao: TimeLogDao) {

    suspend fun addTimeLog(timeLogModel: TimeLogModel) {
        timeLogDao.addTimeLog(timeLogModel)
    }

    suspend fun deleteTimeLog(timeLogModel: TimeLogModel) {
        timeLogDao.deleteTimeLog(timeLogModel)
    }

}