package com.clife.smartutil

import android.app.Application
import android.util.Log

import com.github.anrwatchdog.ANRWatchDog

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ANRWatchDog().start()
        Log.e("MyApplication", "onCreate")
    }
}