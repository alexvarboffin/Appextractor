package com.walhalla.compose.transfer

import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.Executors

/** File offered for download; [name] is what the phone browser saves it as (ASCII-safe). */
class SharedFile(val file: File, name: String) {
    val name: String = name.replace(Regex("[^A-Za-z0-9._-]"), "_")
}

/**
 * Tiny LAN-only HTTP/1.1 server for "send to phone by QR".
 * URLs carry a random token; one file → direct download URL, several → index page.
 */
class LanFileServer(
    private val title: String,
    private val files: List<SharedFile>,
) : Closeable {

    private val token = UUID.randomUUID().toString().take(8)
    private val server = ServerSocket(0)
    private val pool = Executors.newCachedThreadPool()
    @Volatile private var closed = false

    val port: Int get() = server.localPort

    fun start(): LanFileServer {
        pool.execute {
            while (!closed) {
                val socket = runCatching { server.accept() }.getOrNull() ?: break
                pool.execute { runCatching { socket.use(::handle) } }
            }
        }
        return this
    }

    fun url(host: String): String = if (files.size == 1) {
        fileUrl(host, 0)
    } else {
        "http://$host:$port/$token/"
    }

    private fun fileUrl(host: String, index: Int) =
        "http://$host:$port/$token/f/$index/${encode(files[index].name)}"

    private fun handle(socket: Socket) {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.ISO_8859_1))
        val requestLine = reader.readLine() ?: return
        while (true) {
            val header = reader.readLine() ?: break
            if (header.isEmpty()) break
        }
        val parts = requestLine.split(' ')
        val method = parts.getOrNull(0)
        val path = parts.getOrNull(1).orEmpty()
        val out = socket.getOutputStream()
        val headOnly = method == "HEAD"
        if (method != "GET" && !headOnly) {
            out.write(head(405, "text/plain", 0).toByteArray())
            return
        }
        val segments = path.trim('/').split('/')
        when {
            segments.firstOrNull() != token -> out.write(head(404, "text/plain", 0).toByteArray())
            segments.size == 1 -> {
                val body = indexHtml().toByteArray()
                out.write(head(200, "text/html; charset=utf-8", body.size.toLong()).toByteArray())
                if (!headOnly) out.write(body)
            }
            segments.getOrNull(1) == "f" -> {
                val shared = segments.getOrNull(2)?.toIntOrNull()?.let(files::getOrNull)
                if (shared == null || !shared.file.canRead()) {
                    out.write(head(404, "text/plain", 0).toByteArray())
                    return
                }
                out.write(
                    head(
                        200,
                        "application/vnd.android.package-archive",
                        shared.file.length(),
                        "Content-Disposition: attachment; filename=\"${shared.name}\"\r\n"
                    ).toByteArray()
                )
                if (!headOnly) shared.file.inputStream().use { it.copyTo(out, 64 * 1024) }
            }
            else -> out.write(head(404, "text/plain", 0).toByteArray())
        }
        out.flush()
    }

    private fun head(code: Int, type: String, length: Long, extra: String = ""): String {
        val reason = when (code) {
            200 -> "OK"
            404 -> "Not Found"
            else -> "Method Not Allowed"
        }
        return "HTTP/1.1 $code $reason\r\nContent-Type: $type\r\nContent-Length: $length\r\n" +
            "${extra}Connection: close\r\n\r\n"
    }

    private fun indexHtml(): String = buildString {
        append("<!doctype html><html><head><meta charset='utf-8'>")
        append("<meta name='viewport' content='width=device-width,initial-scale=1'>")
        append("<title>").append(escape(title)).append("</title>")
        append("<style>body{font-family:sans-serif;margin:24px}a{display:block;padding:14px;")
        append("margin:8px 0;border:1px solid #888;border-radius:10px;text-decoration:none}</style>")
        append("</head><body><h2>").append(escape(title)).append("</h2>")
        files.forEachIndexed { i, f ->
            append("<a href='f/").append(i).append('/').append(encode(f.name)).append("'>")
            append(escape(f.name)).append(" · ").append(f.file.length() / 1024).append(" KB</a>")
        }
        append("</body></html>")
    }

    override fun close() {
        closed = true
        runCatching { server.close() }
        pool.shutdownNow()
    }

    private fun encode(s: String) = URLEncoder.encode(s, "UTF-8").replace("+", "%20")

    private fun escape(s: String) =
        s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;")

    companion object {
        /** First site-local IPv4 (Wi-Fi / Ethernet); null when offline. */
        fun lanAddress(): String? = runCatching {
            NetworkInterface.getNetworkInterfaces().toList()
                .filter { it.isUp && !it.isLoopback }
                .flatMap { it.inetAddresses.toList() }
                .filterIsInstance<Inet4Address>()
                .firstOrNull { it.isSiteLocalAddress }
                ?.hostAddress
        }.getOrNull()
    }
}
