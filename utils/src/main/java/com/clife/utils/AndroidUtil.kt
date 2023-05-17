package com.clife.utils

import android.util.Log

/**
 * ================================================
 * 作    者：贺志鹏
 * 版    本：com.clife.utils
 * 创建日期： 2023/5/17 9:25
 * 描    述：
 * 修订历史：
 * ================================================
 */
object AndroidUtil {
    private val TAG = "AndroidUtil"
    fun printMessage(message: String) {
        Log.i(TAG, "printMessage: $message")
    }
}