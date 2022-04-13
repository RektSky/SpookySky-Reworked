package ml.rektsky.spookysky.webgui

import com.sun.net.httpserver.HttpServer
import ml.rektsky.spookysky.Client
import java.awt.Desktop
import java.net.InetSocketAddress
import java.net.URI

object HttpServerThread: Thread("server-thread") {

    val server: HttpServer = HttpServer.create(InetSocketAddress("0.0.0.0", WebGui.guiPort), 100)
    val indexHtml: ByteArray = run {
        try {
            val stream = javaClass.classLoader.getResourceAsStream("index.html")!!
            var readBytes = stream.readBytes()
            stream.close()
            readBytes
        } catch (ignored: NullPointerException) {
            "undefined".encodeToByteArray()
        }
    }
    val clientJs: ByteArray = run {
        try {
            val stream = javaClass.classLoader.getResourceAsStream("client.js")!!
            var readBytes = stream.readBytes()
            stream.close()
            readBytes
        } catch (ignored: NullPointerException) {
            "undefined".encodeToByteArray()
        }
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
        if (!Client.debug) {
            Desktop.getDesktop().browse(URI("http://127.0.0.1:${WebGui.guiPort}/index.html"))
        }
        server.start()
    }
}
