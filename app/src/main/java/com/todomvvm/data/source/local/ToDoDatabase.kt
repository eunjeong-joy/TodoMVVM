package com.todomvvm.data.source.local

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.todomvvm.data.Task

@Database(entities = arrayOf(Task::class), version = 1)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun taskDao() : TaskDao

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