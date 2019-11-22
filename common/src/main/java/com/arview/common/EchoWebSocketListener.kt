package com.arview.common

import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class EchoWebSocketListener : WebSocketListener
{
    constructor(ws: IWebSocketListener) : super() {
        webSocketListener = ws
    }

    private val webSocketListener : IWebSocketListener
    private val NORMAL_CLOSURE_STATUS = 1000
    private lateinit var openedWebSocket : WebSocket

    fun sendMessage(message : String)
    {
        openedWebSocket.send(message)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        //super.onOpen(webSocket, response)

        openedWebSocket = webSocket
        openedWebSocket.send("Openning session.")
        openedWebSocket.send("Some random.")
    }

    fun close()
    {
        openedWebSocket.close(NORMAL_CLOSURE_STATUS, "Bye!")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        //super.onMessage(webSocket, text)
        webSocketListener.onMessage ("\n Receiving : " + text)
    }
}