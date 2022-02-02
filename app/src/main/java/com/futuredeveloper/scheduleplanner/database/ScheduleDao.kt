package com.futuredeveloper.scheduleplanner.database

import androidx.room.*

@Dao
interface ScheduleDao {
    @Query("SELECT count(*) FROM schedule_table WHERE schedule_id = :schedule_id")
    fun deleteIfExist(schedule_id: String):Int

    @Insert
    //use suspend fun
    fun insertSchedule(schedule: ScheduleEntity)

    @Update
    fun updateSchedule(schedule: ScheduleEntity)

    @Delete
    //use suspend fun
    fun deleteSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedule_table ORDER BY schedule_id ASC")
    fun getAllSchedule(): List<ScheduleEntity>

    @Query("DELETE FROM schedule_table WHERE schedule_id = :id")
    fun deleteById(id: String)

    @Query("SELECT * FROM schedule_table WHERE schedule_id = :id")
    fun getScheduleById(id: String): ScheduleEntity
}