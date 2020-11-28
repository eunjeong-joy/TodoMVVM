package com.todomvvm.addedittask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.todomvvm.Event
import com.todomvvm.R
import com.todomvvm.data.Task
import com.todomvvm.data.source.TasksDataSource
import com.todomvvm.data.source.TasksRepository
import java.lang.RuntimeException

class AddEditTaskViewModel(private val tasksRepository: TasksRepository)
    : ViewModel(), TasksDataSource.GetTaskCallback {

    val title = MutableLiveData<String>()

    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() =_dataLoading


    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _taskUpdated = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>>
        get() = _taskUpdated

    private var taskId: String? = null

    private var isNewTask: Boolean = false

    private var isDataLoaded = false

    private var taskCompleted = false

    fun start(taskId: String?) {
        _dataLoading.value?.let { isLoading ->
            if (isLoading) return
        }
        this.taskId = taskId
        if (taskId == null) {
            isNewTask = true
            return
        }
        if (isDataLoaded) {
            return
        }
        isNewTask = false
        _dataLoading.value = true

        tasksRepository.getTask(taskId, this)
    }

    override fun onTaskLoaded(task: Task) {
        title.value = task.title
        description.value = task.description
        taskCompleted = task.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }

    override fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    internal fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value =  Event(R.string.empty_task_message)
            return
        }
        if (Task(currentTitle, currentDescription ?: "").isEmpty) {
            _snackbarText.value =  Event(R.string.empty_task_message)
            return
        }

        val currentTaskId = taskId
        if (isNewTask || currentTaskId == null) {
            createTask(Task(currentTitle, currentDescription))
        } else {
            val task = Task(currentTitle, currentDescription, currentTaskId)
                .apply { isCompleted = taskCompleted }
            updateTask(task)
        }
    }

    private fun createTask(newTask: Task) {
        tasksRepository.saveTask(newTask)
        _taskUpdated.value = Event(Unit)
    }

    private fun updateTask(task: Task) {
        if(isNewTask) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        tasksRepository.saveTask(task)
        _taskUpdated.value = Event(Unit)
    }


}