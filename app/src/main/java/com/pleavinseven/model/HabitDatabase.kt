package com.pleavinseven.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [TimeLogModel::class], version = 1)
abstract class HabitDatabase : RoomDatabase() {


    abstract fun timeLogDao(): TimeLogDao

    companion object {
        @Volatile
        var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, HabitDatabase::class.java, "alarm_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}