package com.fabricethilaw.samplesonarnet

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fabricethilaw.sonarnet.ConnectivityCallback
import com.fabricethilaw.sonarnet.ConnectivityResult
import com.fabricethilaw.sonarnet.InternetStatus
import com.fabricethilaw.sonarnet.SonarNet
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

/**
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCheckConnectionStatus = findViewById<Button>(R.id.check)
        val btnToggleLiveNetworkChecking =
            findViewById<Button>(R.id.toggleLiveChecking)

        val dialog = AlertDialog.Builder(this)
        dialog.create()

        btnCheckConnectionStatus.setOnClickListener {
            checkInternet(dialog)
        }

        btnToggleLiveNetworkChecking.setOnClickListener {
            toggleLiveConnectivity(btnToggleLiveNetworkChecking)
        }

    }


    private fun checkInternet(dialog: AlertDialog.Builder) {
        var message: String
        SonarNet.ping { result ->
            message = when (result) {
                InternetStatus.INTERNET -> {
                    "Device is connected to Internet"
                }
                InternetStatus.NO_INTERNET -> {
                    "Device is not connected to Internet"
                }
                InternetStatus.CAPTIVE_PORTAL -> {
                    "A captive portal might be on the network"
                }
            }

            Handler(Looper.getMainLooper()).post { dialog.setMessage(message).show() }
        }
    }

    private fun showConnectivityState(state: ConnectivityResult) {
        Handler(Looper.getMainLooper()).post {
            val message =
                "${state.internetStatus} on ${state.networkType} network"
            Snackbar.make(main, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun toggleLiveConnectivity(toggleButton: Button) {
        toggleButton.text.let {
            if (it == getString(R.string.start)) {

                // instantiate a connectivity callback
                val connectivityCallback = object : ConnectivityCallback {
                    override fun onConnectionChanged(result: ConnectivityResult) {
                        showConnectivityState(result)
                    }
                }
                // register the callback
                SonarNet.with(this).registerConnectivityCallback(connectivityCallback)

                // update button 's text
                toggleButton.text = getString(R.string.stop)
            } else {
                // Disable live checking
                SonarNet.with(this).unregisterConnectivityCallback()
                // update button 's text
                toggleButton.text = getString(R.string.start)

            }
        }
    }
}