package com.clife.smartutil.memory

import android.os.SystemClock
import android.view.Choreographer

object FpsCounter {
    private var lastFrameTimeNanos: Long = 0
    private var framesRendered: Int = 0
    private var startTimeNanos: Long = 0
    private val callback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos != 0L) {
                val timeDiffNanos = frameTimeNanos - lastFrameTimeNanos
                val fps = (1_000_000_000 / timeDiffNanos.toDouble()).toInt()
                // 在这里处理 FPS 值，例如打印到日志或更新 UI
                println("FPS: $fps")
            }
            lastFrameTimeNanos = frameTimeNanos
            framesRendered++
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun start() {
        lastFrameTimeNanos = 0
        framesRendered = 0
        startTimeNanos = SystemClock.elapsedRealtimeNanos()
        Choreographer.getInstance().postFrameCallback(callback)
    }

    fun stop() {
        Choreographer.getInstance().removeFrameCallback(callback)
        val endTimeNanos = SystemClock.elapsedRealtimeNanos()
        val totalTimeSeconds = (endTimeNanos - startTimeNanos) / 1_000_000_000.0
        val averageFps = (framesRendered / totalTimeSeconds).toInt()
        println("Average FPS: $averageFps")
    }
}