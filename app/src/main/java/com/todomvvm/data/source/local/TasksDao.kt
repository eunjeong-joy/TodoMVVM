package com.todomvvm.data.source.local

import androidx.room.*
import com.todomvvm.data.Task

@Dao interface TasksDao {

    @Query("SELECT * FROM Tasks") fun getTasks(): List<Task>

    @Query("SELECT * FROM Tasks WHERE entryid = :taskId") fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task): Int

    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
    fun updateCompleted(taskId: String, completed: Boolean)

    @Query("DELETE FROM Tasks WHERE entryid = :taskId") fun deleteTaskById(taskId: String): Int

    @Query("DELETE FROM Tasks") fun deleteTasks()

    @Query("DELETE FROM Tasks WHERE completed = 1") fun deleteCompletedTasks(): Int
}