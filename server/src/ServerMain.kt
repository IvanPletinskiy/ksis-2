import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch

fun main() {
    val serverSocket = createSocket()
    val countDownLatch = CountDownLatch(1)

    if (serverSocket == null) {
        println("Can't start socket.")
        return
    } else {
        println("Socket started on port ${serverSocket.localPort}, ${serverSocket.inetAddress}")
    }

    val connectionSocket = serverSocket.accept()
    val inputStream = connectionSocket.getInputStream()
    val outputStream = connectionSocket.getOutputStream()

    val bufferedInputReader = inputStream.bufferedReader()
    val keyboardReader = BufferedReader(InputStreamReader(System.`in`))
    val bufferedWriter = outputStream.bufferedWriter()

    val inputThread = Thread {
        while (true) {
            try {

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                countDownLatch.countDown()
                break
            }
            val message = bufferedInputReader.readLine()
            println(message)
        }
    }
    inputThread.start()

    val outputThread = Thread {
        while (true) {
            try {
                val text = keyboardReader.readLine()
                bufferedWriter.write(text + "\n")
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

fun createSocket(): ServerSocket? {
    val bufferedReader = BufferedReader(InputStreamReader(System.`in`))
    println("Enter port number:")

    val portString = bufferedReader.readLine()
    val port = Integer.parseInt(portString)
    var socket: ServerSocket? = null

    try {
        socket = ServerSocket(port)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return socket
}