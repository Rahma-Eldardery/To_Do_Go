package com.example.todogo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey (autoGenerate = true)
    val taskId:Int = 0 ,
    @ColumnInfo(name = "task_category")
    val taskCategory:String,
    @ColumnInfo(name = "task_title")
    val taskTitle:String,
    @ColumnInfo(name = "task_description")
    val taskDescription:String,
    @ColumnInfo(name = "task_is_checked")
    val isChecked: Boolean = false,
    @ColumnInfo(name = "deadline")
    val deadline : Long,
    @ColumnInfo(name = "user_id")
    val userId: String
)

