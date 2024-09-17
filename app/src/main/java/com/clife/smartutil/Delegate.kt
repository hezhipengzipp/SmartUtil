package com.clife.smartutil

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Delegate<T>: ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        TODO("Not yet implemented")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        TODO("Not yet implemented")
    }

}