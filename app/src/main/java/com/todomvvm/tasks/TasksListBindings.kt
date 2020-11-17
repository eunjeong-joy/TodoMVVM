package com.todomvvm.tasks

import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.todomvvm.data.Task

object TasksListBindings {

    @BindingAdapter("items")
    @JvmStatic fun setItems(listView: ListView, items: List<Task>) {
        with(listView.adapter as TasksAdapter) {
            replaceData(items)
        }
    }
}