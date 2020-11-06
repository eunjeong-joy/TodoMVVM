package com.todomvvm

import android.content.Context
import com.todomvvm.data.source.TasksRepository
import com.todomvvm.data.source.local.TasksLocalDataSource
import com.todomvvm.data.source.local.ToDoDatabase
import com.todomvvm.data.source.remote.TasksRemoteDataSource
import com.todomvvm.util.AppExecutors

object Injection {
    fun provideTaskRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(TasksRemoteDataSource,
            TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao()))
    }
}