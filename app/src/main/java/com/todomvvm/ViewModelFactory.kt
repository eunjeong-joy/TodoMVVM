package com.todomvvm

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory private constructor() : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    //TODO : ViewModelFactory instance
                )
            }

        @VisibleForTesting fun destroyInstance() {
            INSTANCE = null
        }
    }
}