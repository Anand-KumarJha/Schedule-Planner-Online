package com.futuredeveloper.scheduleplanner.database

import androidx.room.*

@Dao
interface TaskDao {
        @Insert
        fun insertTask(taskEntity: TaskEntity)

        @Delete
        fun deleteTask(taskEntity: TaskEntity)

        @Update
        fun updateTask(taskEntity: TaskEntity)

        @Query("SELECT * FROM task ORDER BY task_id ASC")
        fun getAllTaskItems(): List<TaskEntity>

        @Query("SELECT * FROM task WHERE task_id = :taskId")
        fun getTaskById(taskId: String): TaskEntity

        @Query("DELETE FROM task WHERE task_id = :taskId")
        fun deleteTaskById(taskId: String)

        @Query("SELECT * FROM task WHERE task_id LIKE :taskDate || '%' ORDER BY task_id ASC")
        fun getTaskByDate(taskDate: String): List<TaskEntity>

        @Query("SELECT * FROM task WHERE task_id LIKE 'R' || '%' ORDER BY task_id ASC")
        fun getAllRepeatTasks(): List<TaskEntity>

        @Query("DELETE FROM task WHERE task_id LIKE :taskDate || '%'")
        fun clearTask(taskDate: String)
}