package com.example.todogo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertTask (taskEntity: TaskEntity)

    @Update
    suspend fun updateTask (taskEntity: TaskEntity)

    @Delete
    suspend fun deleteTask (taskEntity: TaskEntity)

   @Query("SELECT * FROM tasks WHERE user_id = :userId")
    suspend fun getTasksByUser(userId: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE task_category = :category AND user_id = :userId ORDER BY taskId DESC")
    suspend fun getTasksByCategory(category: String, userId: String):List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE taskId = :id AND user_id = :userId")
    suspend fun getTaskById(id: Int, userId: String): TaskEntity


}