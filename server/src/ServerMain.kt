import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

private val connections = mutableListOf<ConnectionHandler>()

typealias OnMessageReceived = (ConnectionHandler, String) -> Unit

fun main() {
    val serverSocket = createSocket()
    val onMessageReceived: OnMessageReceived = { sourceHandler, message ->
        connections.filter {
            it != sourceHandler
        }.forEach {
            it.sendMessage(message)
        }
    }

    if (serverSocket == null) {
        println("Can't start socket.")
        return
    } else {
        println("Socket started on port ${serverSocket.localPort}, ${serverSocket.inetAddress}")
    }

    while (true) {
        val connectionSocket = serverSocket.accept()
        connections.add(ConnectionHandler(connectionSocket, onMessageReceived))
    }
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

class ConnectionHandler(
    socket: Socket,
    private val onMessageReceived: OnMessageReceived
) {
    private val inputStream = socket.getInputStream()
    private val bufferedInputReader = inputStream.bufferedReader()
    private val outputStream = socket.getOutputStream()
    private val bufferedWriter = outputStream.bufferedWriter()

    val inputThread = Thread {
        while (true) {
            val line = bufferedInputReader.readLine()
            onMessageReceived(this, line)
        }
    }.also {
        it.start()
    }

    fun sendMessage(line: String) {
        try {
            bufferedWriter.write(line + "\n")
            bufferedWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}