package com.todomvvm.data.source.local

import android.content.Context
import androidx.room.*
import com.todomvvm.data.Task

@Database(entities = arrayOf(Task::class), version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun taskDao() : TasksDao

    companion object {
        private var INSTANCE: ToDoDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): ToDoDatabase {
            synchronized(lock) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ToDoDatabase::class.java, "Tasks.db")
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}