package io.github.joe2k01.crunchywrap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences(
            resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        if (sharedPref.getString("uuid", "").equals("")) {
            sharedPref.edit().putString("uuid", UUID.randomUUID().toString()).apply()
        }

        ApiCalls(this).authenticate()
        ApiCalls(this).getLocales()

        val main = Intent(this, MainActivity::class.java)
        startActivity(main)
        finish()
    }
}
