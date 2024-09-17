package com.clife.smartutil.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class MyTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    protected fun log(any: Any?) {
        Log.e("AppCompatTextView", any?.toString() ?: "null")
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> log("dispatchTouchEvent ACTION_DOWN")
            MotionEvent.ACTION_MOVE -> log("dispatchTouchEvent ACTION_MOVE")
            MotionEvent.ACTION_UP -> log("dispatchTouchEvent ACTION_UP")
        }
        val flag = super.dispatchTouchEvent(event)
        log("dispatchTouchEvent return: $flag")
        return flag
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                log("onTouchEvent ACTION_DOWN")
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                log("onTouchEvent ACTION_MOVE")
                return false
            }

            MotionEvent.ACTION_UP -> log("onTouchEvent ACTION_UP")
        }
        val flag = super.onTouchEvent(event)
        log("onTouchEvent return: $flag")
        return flag
    }

}
