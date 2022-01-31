package com.futuredeveloper.scheduleplanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
class TaskEntity(
        @PrimaryKey val task_id: String,
        @ColumnInfo(name = "task_time") val taskTime: String,
        @ColumnInfo(name = "task_title") val taskTitle: String,
        @ColumnInfo(name = "task_description") val taskDescription: String,
        @ColumnInfo(name = "task_done") var taskDone: Boolean
)
