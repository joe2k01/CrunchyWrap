package io.github.joe2k01.crunchywrap

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
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

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            ApiCalls(this).authenticate()

            if (!sharedPref.getString("session_id", "").equals("")) {
                ApiCalls(this).getLocales()

                val main = Intent(this, MainActivity::class.java)
                startActivity(main)
                finish()
            } else
                Toast.makeText(this, R.string.went_wrong, Toast.LENGTH_LONG).show()
        } else
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show()
    }
}
