package com.futuredeveloper.scheduleplanner.database

import android.content.Context
import androidx.room.*

@Database(entities = [ScheduleEntity::class], version = 1)
@TypeConverters(TaskTypeConverter::class)

abstract class ScheduleRoomDatabase: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ScheduleRoomDatabase? = null

        fun getDatabase(context: Context): ScheduleRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleRoomDatabase::class.java,
                    "schedule_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}