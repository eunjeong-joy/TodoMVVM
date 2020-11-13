package com.todomvvm.tasks

import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.todomvvm.data.Task

object TasksListBinding {

    @BindingAdapter("app:items")
    @JvmStatic fun setItems(listView: ListView, items: List<Task>) {
        with(listView.adapter as TasksAdapter) {
            replaceData(items)
        }
    }
}