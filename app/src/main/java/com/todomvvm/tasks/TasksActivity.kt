package com.todomvvm.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.todomvvm.Event
import com.todomvvm.R
import com.todomvvm.statistics.StatisticsActivity
import com.todomvvm.util.obtainViewModel
import com.todomvvm.util.setupActionBar

class TasksActivity : AppCompatActivity(), TasksNavigation, TaskItemNavigator {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var viewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        setupNavigationDrawer()


        viewModel = obtainViewModel().apply {
            openTaskEvent.observe(this@TasksActivity, Observer<Event<String>> {event ->
                event.getContentIfNotHandled()?.let {
                    openTaskDetails(it)
                }
            })

            newTaskEvent.observe(this@TasksActivity, Observer<Event<Unit>> {event ->
                event.getContentIfNotHandled()?.let {
                    this@TasksActivity.addNewTask()
                }
            })
        }
    }

    override fun addNewTask() {
    }

    override fun openTaskDetails(taskId: String) {
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setUpDrawerContent(findViewById(R.id.nav_view))
    }

    private fun setUpDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.list_navigation_menu_item -> {

                }
                R.id.statistics_navigation_menu_item -> {
                    val intent = Intent(this@TasksActivity, StatisticsActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupViewFragment() {
    }

    fun obtainViewModel(): TasksViewModel = obtainViewModel(TasksViewModel::class.java)
}