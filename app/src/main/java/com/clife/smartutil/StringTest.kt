package com.clife.smartutil

const val str = "Hello World"
fun main() {
    val str1 = "hello"
    val str2 = "hello"
    val str3 = String("hello".toCharArray())

    println(str1 === str2) // true，因为 str1 和 str2 都指向字符串池中的同一个对象
    println(str1 === str3) // false，因为 str3 是一个新的字符串对象，不在池中

    val str4 = str3.intern() // 将 str3 加入字符串池
    println(str1 === str4) // true，因为 str4 现在指向字符串池中的 "hello" 对象

}