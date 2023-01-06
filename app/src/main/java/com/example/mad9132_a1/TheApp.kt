package com.example.mad9132_a1

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class TheApp : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }

}