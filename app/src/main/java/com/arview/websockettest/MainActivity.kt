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
import com.arview.common.GdaxWebSocketListener
import com.arview.common.json.ParentClass
import com.arview.common.json.ParentClassDto
import com.arview.common.json.SendDataClass
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import okhttp3.Request
import java.lang.Exception
import java.util.*
import android.media.MediaPlayer
import android.os.Environment
import android.net.Uri
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), IWebSocketListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    override fun onPrepared(mp: MediaPlayer?) {
        val snackbar = Snackbar
            .make(mainLayout, "Duration=${play_video_view.duration / 1000}", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
       var wt = what
        var ex = extra
        return true
    }

    private val REQUEST_CODE_READ_EXTERNAL_PERMISSION: Int = 1000
    private var echoWebSocketListener : EchoWebSocketListener? = null
    private var coinGdaxWebSocketListener : GdaxWebSocketListener? = null
    private val MY_PERMISSIONS_REQUEST_INTERNET : Int = 1000
    private lateinit var sendWebSocket : okhttp3.WebSocket
    private lateinit var coinWebSocket : okhttp3.WebSocket
    private val gdaxUrl = "wss://ws-feed.gdax.com"
    private val echoUrl = "ws://echo.websocket.org"
    private var gson : Gson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                MY_PERMISSIONS_REQUEST_INTERNET)
        }

        gson = GsonBuilder().setPrettyPrinting().create()
        play_video_view.setOnErrorListener(this)
        play_video_view.setOnPreparedListener(this)

//        val person = Person("Kolineer", 27, listOf("I am Kotlin Learner", "At Kotlination"))
//        val jsonPerson: String = gson.toJson(person)

        if (echoWebSocketListener == null)
        {
            echoWebSocketListener = EchoWebSocketListener(this)
            val client = OkHttpClient()
            val request = Request.Builder().url(echoUrl).build()

            sendWebSocket = client.newWebSocket(request, echoWebSocketListener!!)
        }

//        if (coinGdaxWebSocketListener == null)
//        {
//            coinGdaxWebSocketListener = GdaxWebSocketListener(this)
//            val client = OkHttpClient()
//            val request = Request.Builder().url(gdaxUrl).build()
//
//            coinWebSocket = client.newWebSocket(request, coinGdaxWebSocketListener!!)
//        }

        send_button.setOnClickListener {
            echoWebSocketListener!!.sendMessage(send_message_edittext.text.toString())
            send_message_edittext.setText("")
        }

        send_data_button.setOnClickListener {
            echoWebSocketListener!!.sendMessage(generateSendDataMessage())
        }

        output_button.setOnClickListener {
            try {

                // Check whether user has granted read external storage permission to this activity.
                val readExternalStoragePermission = ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                )

                // If not grant then require read external storage permission.
                if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    val requirePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(
                        this,
                        requirePermission,
                        REQUEST_CODE_READ_EXTERNAL_PERMISSION
                    )
                } else {

                   var list = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles()
                    var path = list[0].path

                    //play_video_view.setVideoPath(path);
                    val webVideoFileUri = Uri.parse(path.trim())

                    // Play web video file use the Uri object.
                    play_video_view.setVideoURI(webVideoFileUri)
                    play_video_view.start();

                }
            }
            catch (ex: Exception)
            {

            }

        }

    }

    private fun generateSendDataMessage() : String{

        val parentClass = ParentClass(1,"Arview",Date( 2019,11,25),
            sendDataClass = SendDataClass("send", 3)
        )

            val jsonSendDataClass: String = gson!!.toJson(parentClass)
            return jsonSendDataClass
    }


    override fun onDestroy() {
        super.onDestroy()

        echoWebSocketListener?.close()
    }

    override fun onMessage(text: String) {

        try {
            val ParentDataClass : ParentClassDto = gson!!.fromJson(text, ParentClassDto::class.java)
            log_text_view.append("\ninputJson :" + text + "\nDate is :" + ParentDataClass.recordDate  + "\nSendData age is: ${ParentDataClass.sendDataClass!!.age}")

        }
        catch (exc : JsonSyntaxException)
        {
            try {
                log_text_view.append(text)
            }
            catch (exc: Exception)
            {

            }
        }


    }
}
