package io.github.joe2k01.crunchywrap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_streaming.*

class StreamingActivity : AppCompatActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val url = intent.getStringExtra("url")!!
                videoView.setVideoURI(Uri.parse(url))
                videoView.start()

                videoView.setOnPreparedListener {
                    progress.max = videoView.duration

                    loading_v.visibility = View.GONE
                }

                videoView.setOnClickListener {
                    if (controls.isVisible)
                        controls.visibility = View.GONE
                    else {
                        progress.progress = videoView.currentPosition
                        controls.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)

        progress_circular.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            ), PorterDuff.Mode.SRC_IN
        )
        progress.progressDrawable.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )
        progress.thumb.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )

        LocalBroadcastManager.getInstance(baseContext)
            .registerReceiver(receiver, IntentFilter(ApiCalls(baseContext).URL_INTENT))

        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        ApiCalls(baseContext).getStreamingLink(intent.getStringExtra("id")!!)

        progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

            override fun onProgressChanged(p0: SeekBar?, position: Int, user: Boolean) {
                if (user)
                    videoView.seekTo(position)
            }
        })

        play.setOnClickListener {
            if (videoView.isPlaying) {
                play.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
                videoView.pause()
            } else {
                play.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
                videoView.start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(receiver)

        super.onDestroy()
    }
}
