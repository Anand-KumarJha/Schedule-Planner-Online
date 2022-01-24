package com.futuredeveloper.scheduleplanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.futuredeveloper.scheduleplanner.models.Task
import com.google.gson.Gson
import java.lang.StringBuilder

@Entity(tableName = "schedule_table")
class ScheduleEntity (
    @PrimaryKey val schedule_id: String,
    @ColumnInfo(name = "schedule_date") val scheduleDate: String,
    @ColumnInfo(name = "schedule_title") val scheduleTitle: String,
    @ColumnInfo(name = "schedule_date_notes") val scheduleDateNotes: String,
    @ColumnInfo(name = "tasks") val tasks: List<Task>
)

class TaskTypeConverter{
    @TypeConverter
    fun listToJson(value: List<Task>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value,Array<Task>::class.java).toList()
}