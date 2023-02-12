package com.example.appstudents

import android.app.Application
import android.content.Context

class AppStudentIntendApplication: Application() {

    init {
        instance = this
    }

    companion object{
        private var instance: AppStudentIntendApplication?= null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

}