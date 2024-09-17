package com.clife.smartutil

import android.app.Application
import android.util.Log
import com.didichuxing.doraemonkit.DoKit

import com.github.anrwatchdog.ANRWatchDog

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ANRWatchDog().start()
        DoKit.Builder(this)
            .build()
        Log.e("MyApplication", "onCreate")
    }
}