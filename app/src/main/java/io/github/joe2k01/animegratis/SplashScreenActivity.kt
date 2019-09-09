package io.github.joe2k01.animegratis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiCalls(this).authenticate()

        val main = Intent(this, MainActivity::class.java)
        startActivity(main)
        finish()
    }
}
