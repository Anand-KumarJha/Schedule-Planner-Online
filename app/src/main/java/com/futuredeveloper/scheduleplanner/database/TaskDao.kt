package com.futuredeveloper.scheduleplanner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
        @Insert
        fun insertTask(taskEntity: TaskEntity)

        @Delete
        fun deleteTask(taskEntity: TaskEntity)

        @Query("SELECT * FROM task")
        fun getAllTaskItems(): List<TaskEntity>

        @Query("SELECT * FROM task WHERE task_id = :taskId")
        fun getTaskById(taskId: String): TaskEntity

        @Query("SELECT * FROM task WHERE task_id LIKE :taskDate || '%'")
        fun getTaskByDate(taskDate: String): List<TaskEntity>

        @Query("DELETE FROM task")
        fun clearTask()
}