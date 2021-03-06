package com.todomvvm

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todomvvm.data.source.TasksRepository
import com.todomvvm.tasks.TasksViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(TasksViewModel::class.java) ->
                    TasksViewModel(tasksRepository)

                else -> throw IllegalArgumentException("Unknown ViewModel Class : ${modelClass.name}")
            }
        } as T

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideTaskRepository(application.applicationContext)
                ).also { INSTANCE = it }
            }

        @VisibleForTesting fun destroyInstance() {
            INSTANCE = null
        }
    }
}