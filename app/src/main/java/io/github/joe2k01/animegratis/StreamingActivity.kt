package io.github.joe2k01.animegratis

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_streaming.*

class StreamingActivity : AppCompatActivity() {

    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)

        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        val url = ApiCalls().getStreamingLink(intent.getStringExtra("id")!!)
        videoView.setVideoURI(Uri.parse(url))
        videoView.start()

        videoView.setOnPreparedListener {
            progress.max = videoView.duration
        }

        videoView.setOnClickListener {
            if (controls.isVisible)
                controls.visibility = View.GONE
            else {
                progress.progress = videoView.currentPosition
                controls.visibility = View.VISIBLE
            }
        }

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
}
