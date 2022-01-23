package com.futuredeveloper.scheduleplanner.models

data class Schedule(
    val scheduleId: String,
    val scheduleDate: String,
    val scheduleNotes: String,
    val scheduleTasks: List<Task>
)