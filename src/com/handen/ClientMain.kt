package com.handen

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.CountDownLatch

fun main() {
    val bufferedReader = BufferedReader(InputStreamReader(System.`in`))
    val countDownLatch = CountDownLatch(1)
    println("Enter ip address:")
    val ipString = bufferedReader.readLine() //Enter "localhost"
    val ip = InetAddress.getByName(ipString)
    println("Enter port:")
    val portString = bufferedReader.readLine()
    val port = Integer.parseInt(portString)
    val socket = Socket(ip, port)
    println("Enter your name:")
    val name = bufferedReader.readLine()

    val inputStream = socket.getInputStream()
    val outputStream = socket.getOutputStream()

    val bufferedInputReader = inputStream.bufferedReader()
    val keyboardReader = BufferedReader(InputStreamReader(System.`in`))
    val bufferedWriter = outputStream.bufferedWriter()

    val inputThread = Thread {
        while (true) {
            try {
                val message = bufferedInputReader.readLine()
                println(message)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                countDownLatch.countDown()
                break
            }
        }
    }
    inputThread.start()

    val outputThread = Thread {
        while (true) {
            try {
                val text = keyboardReader.readLine()
                bufferedWriter.write("$name:\t$text\n")
                bufferedWriter.flush()
            } catch (e: Exception) {
                e.printStackTrace()
                countDownLatch.countDown()
                break
            }
        }
    }
    outputThread.start()

    countDownLatch.await()
}