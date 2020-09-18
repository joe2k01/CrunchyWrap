package io.github.joe2k01.crunchywrap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_streaming.*

class StreamingActivity : AppCompatActivity() {
    private lateinit var updateProgress: Runnable
    private lateinit var myHandler: Handler
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val url = intent.getStringExtra("url")!!
                if (url != "") {
                    videoView.setVideoURI(Uri.parse(url))
                    videoView.start()

                    videoView.setOnPreparedListener {
                        progress.max = videoView.duration

                        loading_v.visibility = View.GONE

                        myHandler.postDelayed(updateProgress, 1000)
                    }

                    videoView.setOnClickListener {
                        if (controls.isVisible)
                            controls.visibility = View.GONE
                        else {
                            progress.progress = videoView.currentPosition
                            controls.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Snackbar.make(controls, R.string.went_wrong, Snackbar.LENGTH_INDEFINITE)
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
            .registerReceiver(receiver, IntentFilter(ApiCalls(baseContext).urlIntent))

        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        ApiCalls(baseContext).getStreamingLink(
            intent.getStringExtra("id")!!,
            intent.getStringExtra("locale")!!
        )

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
                play.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_play
                    )
                )
                videoView.pause()
            } else {
                play.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_pause
                    )
                )
                videoView.start()
            }
        }

        myHandler = Handler(Looper.getMainLooper())
        updateProgress = Runnable {
            progress.progress = videoView.currentPosition
            myHandler.postDelayed(updateProgress, 1000)
        }
    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(receiver)
        myHandler.removeCallbacks(updateProgress)

        super.onDestroy()
    }
}
