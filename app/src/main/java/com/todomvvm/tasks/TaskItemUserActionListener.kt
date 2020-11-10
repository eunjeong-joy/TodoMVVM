package com.todomvvm.tasks

import android.view.View
import com.todomvvm.data.Task

interface TaskItemUserActionListener {
    fun onCompleteChanged(task: Task, v: View)

    fun onTaskClicked(task: Task)
}