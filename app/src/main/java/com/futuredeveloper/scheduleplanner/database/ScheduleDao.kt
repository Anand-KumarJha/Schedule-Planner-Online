package com.futuredeveloper.scheduleplanner.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScheduleDao {
    @Insert
    //use suspend fun
    fun insertSchedule(schedule: ScheduleEntity)

    @Delete
    //use suspend fun
    fun deleteSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedule_table")
    fun getAllSchedule(): List<ScheduleEntity>
}