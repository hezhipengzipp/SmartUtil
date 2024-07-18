package com.clife.smartutil

class SayException : ISay {
    override fun saySomething(): String {
        return "something wrong here"
    }
}