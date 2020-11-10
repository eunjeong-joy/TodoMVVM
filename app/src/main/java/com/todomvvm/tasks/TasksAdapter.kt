package com.todomvvm.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import com.todomvvm.data.Task
import com.todomvvm.databinding.TaskItemBinding
import java.lang.IllegalStateException

class TasksAdapter (
    private var tasks: List<Task>,
    private var tasksViewModel: TasksViewModel
    ) : BaseAdapter() {

    fun replaceData(tasks: List<Task>) {
        setList(tasks)
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: TaskItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            TaskItemBinding.inflate(inflater, view, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val userActionListener = object: TaskItemUserActionListener {
            override fun onCompleteChanged(task: Task, v: View) {
                val checked = (v as CheckBox).isChecked
                tasksViewModel.completeTask(task, checked)
            }

            override fun onTaskClicked(task: Task) {
                tasksViewModel.openTask(task.id)
            }
        }

        with(binding) {
            task = tasks[position]
            listener = userActionListener
            executePendingBindings()
        }

        return binding.root
    }

    override fun getItem(position: Int) = tasks[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = tasks.size

    private fun setList(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}