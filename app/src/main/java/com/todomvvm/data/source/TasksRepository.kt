package com.todomvvm.data.source

import com.todomvvm.data.Task

class TasksRepository (
    val tasksRemoteDataSource: TasksDataSource,
    val tasksLocalDataSource: TasksDataSource
) : TasksDataSource {

    var cachedTasks: LinkedHashMap<String, Task> = LinkedHashMap()

    var cacheIsDirty = false

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {

        if(cachedTasks.isNotEmpty() && !cacheIsDirty) {
            callback.onTasksLoaded(ArrayList(cachedTasks.values))
            return
        }

        if(cacheIsDirty) {
            getTasksFromRemoteDataSource(callback)
        } else {
            tasksLocalDataSource.getTasks(object : TasksDataSource.LoadTasksCallback{
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    callback.onTasksLoaded(ArrayList(cachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback)
                }
            })
        }

    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val taskInCache = getTaskWithId(taskId)

        if(taskInCache != null) {
            callback.onTaskLoaded(taskInCache)
            return
        }

        tasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback{
            override fun onTaskLoaded(task: Task) {
                cacheAndPerform(task) {
                    callback.onTaskLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback{
                    override fun onTaskLoaded(task: Task) {
                        cacheAndPerform(task) {
                            callback.onTaskLoaded(it)
                        }
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun saveTask(task: Task) {
        cacheAndPerform(task) {
            tasksRemoteDataSource.saveTask(it)
            tasksLocalDataSource.saveTask(it)
        }
    }

    override fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            tasksRemoteDataSource.completeTask(it)
            tasksLocalDataSource.completeTask(it)
        }
    }

    override fun completeTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = false
            tasksRemoteDataSource.activateTask(it)
            tasksLocalDataSource.activateTask(it)
        }
    }

    override fun activateTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            activateTask(it)
        }
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        cachedTasks = cachedTasks.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)
        cachedTasks.remove(taskId)
    }

    private fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadTasksCallback) {
        tasksRemoteDataSource.getTasks(object : TasksDataSource.LoadTasksCallback{
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
                refreshLocalDataSource(tasks)

                callback.onTasksLoaded(ArrayList(cachedTasks.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.forEach {
            cacheAndPerform(it){}
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        for(task in tasks) {
            tasksLocalDataSource.saveTask(task)
        }
    }

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cachedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        cachedTasks.put(cachedTask.id, cachedTask)
        perform(cachedTask)
    }

    private fun getTaskWithId(id: String) = cachedTasks[id]
}