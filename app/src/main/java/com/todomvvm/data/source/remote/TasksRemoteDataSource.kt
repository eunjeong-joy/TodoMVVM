package com.todomvvm.data.source.remote

import android.os.Handler
import com.todomvvm.data.Task
import com.todomvvm.data.source.TasksDataSource
import com.google.common.collect.Lists


object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var TASK_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Build tower in Paris", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASK_SERVICE_DATA.put(newTask.id, newTask)
    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        val tasks = Lists.newArrayList(TASK_SERVICE_DATA.values)
        Handler().postDelayed({
            callback.onTasksLoaded(tasks)
        }, SERVICE_LATENCY_IN_MILLIS)
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASK_SERVICE_DATA[taskId]

        with(Handler()) {
            if( task != null ) {
                postDelayed({ callback.onTaskLoaded(task) }, SERVICE_LATENCY_IN_MILLIS)
            } else {
                postDelayed({ callback.onDataNotAvailable() }, SERVICE_LATENCY_IN_MILLIS)
            }
        }
    }

    override fun saveTask(task: Task) {
        TASK_SERVICE_DATA.put(task.id, task)
    }

    override fun completeTask(task: Task) {
        val completeTask = Task(task.title, task.description, task.id).apply {
            isCompleted = true
        }
        TASK_SERVICE_DATA.put(task.id, completeTask)
    }

    override fun completeTask(taskId: String) {}

    override fun activateTask(task: Task) {
        val activateTask = Task(task.title, task.description, task.id)
        TASK_SERVICE_DATA.put(task.id, activateTask)
    }

    override fun activateTask(taskId: String) {}

    override fun clearCompletedTasks() {
        TASK_SERVICE_DATA = TASK_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {}

    override fun deleteAllTasks() {
        TASK_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASK_SERVICE_DATA.remove(taskId)
    }
}