package com.futuredeveloper.scheduleplanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(tableName = "schedule_table")
class ScheduleEntity (
    @PrimaryKey val schedule_id: String,
    @ColumnInfo(name = "schedule_date") val scheduleDate: String,
    @ColumnInfo(name = "schedule_title") val scheduleTitle: String,
    @ColumnInfo(name = "schedule_date_notes") val scheduleDateNotes: String,
    @ColumnInfo(name = "tasks") val tasks: List<TaskEntity>
)

class TaskTypeConverter{
    @TypeConverter
    fun listToJson(value: List<TaskEntity>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value,Array<TaskEntity>::class.java).toList()
}