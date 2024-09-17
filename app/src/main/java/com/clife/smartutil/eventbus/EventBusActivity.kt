package com.clife.smartutil.eventbus

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clife.smartutil.R
import dalvik.system.DexClassLoader
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Proxy

class EventBusActivity : AppCompatActivity() {
    val name: String by lazy {
        "hello"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_bus)
        EventBus.getDefault().post(MessageEvent())

        initSp()
        val numbers = listOf(1, -2, 3, -4, 5, -6)
        val positives = numbers.filter { x -> x > 0 }
        val negatives = numbers.filter { x -> x < 0 }
        println(positives)
    }

    @SuppressLint("CommitPrefEdits")
    private fun initSp() {
        val sharedPreferences = getSharedPreferences("smart_util", MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putString("key", "value")
        edit.apply()
        edit.commit()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {

    }


}