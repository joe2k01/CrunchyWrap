package io.github.joe2k01.crunchywrap

import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (!sharedPref.getString("session_id", "").equals("")) {
                    ApiCalls(baseContext).getLocales()

                    val main = Intent(baseContext, MainActivity::class.java)
                    startActivity(main)
                    finish()
                } else
                    Toast.makeText(baseContext, R.string.went_wrong, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(
            resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        if (sharedPref.getString("uuid", "").equals("")) {
            sharedPref.edit().putString("uuid", UUID.randomUUID().toString()).apply()
        }

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                try {
                    ProviderInstaller.installIfNeeded(this)
                } catch (e: GooglePlayServicesRepairableException) {
                    // Indicates that Google Play services is out of date, disabled, etc.

                    // Prompt the user to install/update/enable Google Play services.
                    GoogleApiAvailability.getInstance()
                        .showErrorNotification(this, e.connectionStatusCode)
                } catch (e: GooglePlayServicesNotAvailableException) {
                    // Indicates a non-recoverable error; the ProviderInstaller is not able
                    // to install an up-to-date Provider.
                    Toast.makeText(this, R.string.outdated, Toast.LENGTH_LONG).show()
                }
            }
            LocalBroadcastManager.getInstance(baseContext)
                .registerReceiver(receiver, IntentFilter(ApiCalls(baseContext).authenticateIntent))

            ApiCalls(this).authenticate()
        } else
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show()
    }
}
