package ml.rektsky.spookysky.webgui

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

object HttpServerThread: Thread("server-thread") {

    val server: HttpServer = HttpServer.create(InetSocketAddress("0.0.0.0", WebGui.guiPort), 100)
    val indexHtml: ByteArray = run {
        val stream = javaClass.classLoader.getResourceAsStream("index.html")!!
        var readBytes = stream.readBytes()
        stream.close()
        readBytes
    }
    val clientJs: ByteArray = run {
        val stream = javaClass.classLoader.getResourceAsStream("client.js")!!
        var readBytes = stream.readBytes()
        stream.close()
        readBytes
    }


    override fun run() {
        server.createContext("/index.html") { exchange ->
            exchange.responseHeaders["Access-Control-Allow-Origin"] = "*"
            exchange.sendResponseHeaders(200, String(indexHtml).length.toLong())
            exchange.responseBody.write(indexHtml)
            exchange.responseBody.close()
        }
        server.createContext("/client.js") { exchange ->
            exchange.responseHeaders["Access-Control-Allow-Origin"] = "*"
            exchange.sendResponseHeaders(200, String(clientJs).length.toLong())
            exchange.responseBody.write(clientJs)
            exchange.responseBody.close()
        }
        server.start()
    }
}
