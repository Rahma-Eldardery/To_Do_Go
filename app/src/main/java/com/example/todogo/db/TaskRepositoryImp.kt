package com.example.todogo.db

class TaskRepositoryImp (private val dataBase: TaskDataBase) : TaskRepository {
    override suspend fun insertTask(taskEntity: TaskEntity) {
       dataBase.taskDao().insertTask(taskEntity)
    }

    override suspend fun updateTask(taskEntity: TaskEntity) {
        dataBase.taskDao().updateTask(taskEntity)
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        dataBase.taskDao().deleteTask(taskEntity)
    }

    override suspend fun getTasksByUser(userId: String): List<TaskEntity> {
        return dataBase.taskDao().getTasksByUser(userId)
    }

    override suspend fun getTasksByCategory(category: String, userId: String): List<TaskEntity> {
        return dataBase.taskDao().getTasksByCategory(category, userId)
    }

    override suspend fun getTaskById(id: Int, userId: String): TaskEntity {

        return dataBase.taskDao().getTaskById(id, userId)

    }


}