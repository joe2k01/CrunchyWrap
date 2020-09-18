package io.github.joe2k01.crunchywrap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_streaming.*

class StreamingActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val url = intent.getStringExtra("url")!!
                if (url != "") {
                    val mediaItem = MediaItem.fromUri(url)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                } else {
                    Snackbar.make(streamingView, R.string.went_wrong, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok) {
                            finish()
                        }
                        .show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        playerView.setShowBuffering(SHOW_BUFFERING_ALWAYS)

        LocalBroadcastManager.getInstance(baseContext)
            .registerReceiver(receiver, IntentFilter(ApiCalls(baseContext).urlIntent))

        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        ApiCalls(baseContext).getStreamingLink(
            intent.getStringExtra("id")!!,
            intent.getStringExtra("locale")!!
        )
    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(receiver)
        player.stop(true)

        super.onDestroy()
    }
}
