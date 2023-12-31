package com.pleavinseven.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pleavinseven.model.entities.DailyCount
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.model.entities.Habit

@Database(entities = [TimeLogModel::class, Habit::class, DailyCount::class], version = 6)
abstract class HabitDatabase : RoomDatabase() {


    abstract fun timeLogDao(): TimeLogDao
    abstract fun habitDao(): HabitDao
    abstract fun dailyCountDao(): DailyCountDao

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