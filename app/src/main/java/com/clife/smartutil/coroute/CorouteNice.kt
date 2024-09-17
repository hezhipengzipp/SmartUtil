package com.clife.smartutil.coroute

import android.util.SparseArray
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }

}
fun main(): Unit = runBlocking {
//    launch(Dispatchers.IO) {
//        println("IO runBlocking      : I'm working in thread ${Thread.currentThread().name}")
//    }
//    launch(Dispatchers.Default) {
//        println("Default runBlocking      : I'm working in thread ${Thread.currentThread().name}")
//    }
//    launch(Dispatchers.Unconfined) {
//        println("Unconfined runBlocking      : I'm working in thread ${Thread.currentThread().name}")
//    }
//    async(CoroutineName("haha")) {

//    }


    val channel = Channel<String>() // 用于客户端和服务器之间通信的 Channel

    // 服务器协程
    launch {
        for (message in channel) {
            println("Server received:$message")
            if (message == "Bye") break
            val response = "Hello, $message!"
            channel.send(response) // 发送响应给客户端
        }
        println("Server closing...")
    }

    // 客户端协程
    launch {
        val messages = listOf("Alice", "Bob", "Charlie", "Bye")
        for (message in messages) {
            println("Client sending: $message")
            channel.send(message) // 发送消息给服务器
            val response = channel.receive() // 接收服务器的响应println("Client received: $response")
        }
        println("Client closing...")
    }
}