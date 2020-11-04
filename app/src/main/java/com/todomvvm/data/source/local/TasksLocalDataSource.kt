package com.todomvvm.data.source.local

import com.todomvvm.data.Task
import com.todomvvm.data.source.TasksDataSource
import com.todomvvm.util.AppExecutors

class TasksLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val taskDao: TaskDao
) : TasksDataSource {

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        appExecutors.diskIO.execute {
            val tasks = taskDao.getTasks()
            appExecutors.mainThread.execute {
                if(tasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(tasks)
                }
            }
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        appExecutors.diskIO.execute {
            val task = taskDao.getTaskById(taskId)
            appExecutors.mainThread.execute {
                if(task != null) {
                    callback.onTaskLoaded(task)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveTask(task: Task) {
        appExecutors.diskIO.execute {
            taskDao.insertTask(task)
        }
    }

    override fun completeTask(task: Task) {
        appExecutors.diskIO.execute {
            taskDao.updateComplete(task.id, true)
        }
    }

    override fun completeTask(taskId: String) {}

    override fun activateTask(task: Task) {
        appExecutors.diskIO.execute {
            taskDao.updateComplete(task.id, false)
        }
    }

    override fun activateTask(taskId: String) {}

    override fun clearCompletedTasks() {
        appExecutors.diskIO.execute {
            taskDao.deleteCompletedTasks()
        }
    }

    override fun refreshTasks() {}

    override fun deleteAllTasks() {
        appExecutors.diskIO.execute {
            taskDao.deleteTasks()
        }
    }

    override fun deleteTask(taskId: String) {
        appExecutors.diskIO.execute {
            taskDao.deleteTaskById(taskId)
        }
    }
}