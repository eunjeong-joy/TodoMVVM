package com.todomvvm.tasks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.todomvvm.Event
import com.todomvvm.R
import com.todomvvm.data.Task
import com.todomvvm.data.source.TasksDataSource
import com.todomvvm.data.source.TasksRepository

class TasksViewModel(private val tasksRepository: TasksRepository): ViewModel() {
    private val _items = MutableLiveData<List<Task>>().apply {
        value = emptyList()
    }
    val items: LiveData<List<Task>>
        get() = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int>
        get() = _currentFilteringLabel

    private val _noTasksLabel = MutableLiveData<Int>()
    val noTasksLabel: LiveData<Int>
        get() = _noTasksLabel

    private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int>
        get() = _noTaskIconRes

    private val _tasksAddViewVisible = MutableLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean>
        get() = _tasksAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    private var _currentFiltering = TasksFilterType.ALL_TASKS

    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>>
        get() = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>>
        get() = _newTaskEvent

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        setFiltering(TasksFilterType.ALL_TASKS)
    }

    fun start() {
        loadTasks(true)
    }

    fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate, true)
    }

    fun setFiltering(requestType: TasksFilterType) {
        _currentFiltering = requestType

        when(requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(R.string.label_all, R.string.no_tasks_all, R.drawable.ic_assignment_turned_in_24dp, true)
            }
            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(R.string.label_active, R.string.no_tasks_active, R.drawable.ic_check_circle_24dp, false)
            }
            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(R.string.label_completed, R.string.no_tasks_completed, R.drawable.ic_verified_user_24dp, false)
            }
        }
    }

    private fun setFilter(@StringRes filteringLabelString: Int, @StringRes noTaskLabelString: Int,
                            @DrawableRes noTaskIconDrawable: Int, tasksAddVisible: Boolean) {
        _currentFilteringLabel.value = filteringLabelString
        _noTasksLabel.value = noTaskLabelString
        _noTaskIconRes.value = noTaskIconDrawable
        _tasksAddViewVisible.value = tasksAddVisible
    }

    fun clearCompletedTasks() {
        tasksRepository.clearCompletedTasks()
        _snackbarText.value = Event(R.string.completed_tasks_cleared)
        loadTasks(false, false)
    }

    fun completeTask(task: Task, completed: Boolean) {
        if(completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    internal fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }

    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if(showLoadingUI) {
            _dataLoading.value = true
        }

        if(forceUpdate) {
            tasksRepository.refreshTasks()
        }

        tasksRepository.getTasks(object: TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = ArrayList<Task>()

                for(task in tasks) {
                    when(_currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                    }
                }

                if(showLoadingUI) {
                    _dataLoading.value = false
                }
                isDataLoadingError.value = false

                val itemValue = ArrayList(tasksToShow)
                _items.value = itemValue
            }

            override fun onDataNotAvailable() {
                isDataLoadingError.value = true
            }
        })
    }

    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

}