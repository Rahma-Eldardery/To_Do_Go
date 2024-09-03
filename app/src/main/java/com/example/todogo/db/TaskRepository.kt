package com.example.todogo.db

import androidx.room.Query

interface TaskRepository {

    suspend fun insertTask (taskEntity: TaskEntity)

    suspend fun updateTask (taskEntity: TaskEntity)

    suspend fun deleteTask (taskEntity: TaskEntity)

    suspend fun getTasksByUser(userId: String): List<TaskEntity>

    suspend fun getTasksByCategory(category: String, userId: String):List<TaskEntity>

    suspend fun getTaskById(id: Int, userId: String): TaskEntity


}