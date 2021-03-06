package io.getstream.chat.android.client.sample.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.events.*
import io.getstream.chat.android.client.utils.observable.Subscription
import io.getstream.chat.android.client.sample.R
import kotlinx.android.synthetic.main.activity_socket_tests.*
import java.text.SimpleDateFormat
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SocketTestActivity : AppCompatActivity() {

    var sub: Subscription? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_tests)

        val client = ChatClient.instance()

        btnConnect.setOnClickListener {

            textSocketState.text = "Connecting..."

            sub = client.events().subscribe {

                Log.d("evt", it.type)
                appendEvent(it)

                when (it) {
                    is ConnectedEvent -> {
                        textSocketState.text = "Connected"
                        Toast.makeText(this, "Connection resolved", Toast.LENGTH_SHORT).show()
                    }
                    is ErrorEvent -> {
                        Log.d("error", it.error.toString())
                        Toast.makeText(this, "Error event", Toast.LENGTH_SHORT).show()
                    }
                    is ConnectingEvent -> {
                        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show()
                    }
                    is DisconnectedEvent -> {
                        textSocketState.text = "Disconnected"
                        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Some event", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val token =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiYmVuZGVyIn0.3KYJIoYvSPgTURznP8nWvsA2Yj2-vLqrm-ubqAeOlcQ"

            client.setUser(User("bender"), token)
        }

        btnDisconnect.setOnClickListener {
            textSocketState.text = "Disconnected"
            client.disconnect()
            sub?.unsubscribe()
        }


    }

    override fun onDestroy() {
        sub?.unsubscribe()
        super.onDestroy()
    }

    private val sb = StringBuilder()
    private val logTimeFormat = SimpleDateFormat("hh:mm:ss")

    private fun appendEvent(event: ChatEvent) {
        val date = event.receivedAt
        val log = logTimeFormat.format(date) + ":" + event.type + "\n"
        sb.insert(0, log)
        textSocketEvent.text = sb.toString()
    }
}