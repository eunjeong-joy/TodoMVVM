package com.todomvvm.tasks

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.todomvvm.R
import com.todomvvm.databinding.TasksFragBinding

class TasksFragment : Fragment() {

    private lateinit var viewDataBinding: TasksFragBinding
    private lateinit var listAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as TasksActivity).obtainViewModel()
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.menu_clear -> {
                viewDataBinding.viewmodel?.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopupMenu()
                true
            }
            R.id.menu_refresh -> {
                viewDataBinding.viewmodel?.loadTasks(true)
                true
            }
            else -> false
        }

    private fun showFilteringPopupMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.tasks_fragment_menu, menu)

            setOnMenuItemClickListener {
                viewDataBinding.viewmodel?.run {
                    setFiltering(
                        when(it.itemId) {
                            R.id.active -> TasksFilterType.ACTIVE_TASKS
                            R.id.completed -> TasksFilterType.COMPLETED_TASKS
                            else -> TasksFilterType.ALL_TASKS
                        })
                    loadTasks(false)
                }
                true
            }
            show()
        }
    }
}