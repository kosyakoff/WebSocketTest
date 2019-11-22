package com.arview.websockettest

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.arview.common.IWebSocketListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import com.arview.common.EchoWebSocketListener
import androidx.core.content.ContextCompat
import okhttp3.Request


class MainActivity : AppCompatActivity(), IWebSocketListener {

    private var echoWebSocketListener : EchoWebSocketListener? = null
    private val MY_PERMISSIONS_REQUEST_INTERNET : Int = 1000
    private lateinit var webSocket : okhttp3.WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                MY_PERMISSIONS_REQUEST_INTERNET)
        }

        if (echoWebSocketListener == null)
        {
            echoWebSocketListener = EchoWebSocketListener(this)
            val client = OkHttpClient()
            val request = Request.Builder().url("ws://echo.websocket.org").build()
            webSocket = client.newWebSocket(request, echoWebSocketListener!!)
        }

        send_button.setOnClickListener {
            echoWebSocketListener!!.sendMessage(send_message_edittext.text.toString())
            send_message_edittext.setText("")
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        echoWebSocketListener?.close()
    }

    override fun onMessage(text: String) {
        log_text_view.append(text)
    }
}
