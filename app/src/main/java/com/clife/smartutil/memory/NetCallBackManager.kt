package com.clife.smartutil.memory

object NetCallBackManager {
    private var listCallback = mutableListOf<NetCallback>()
    fun addCallback(callback: NetCallback) {
        listCallback.add(callback)
    }

    fun removeCallback(callback: NetCallback) {
        listCallback.remove(callback)
    }
}