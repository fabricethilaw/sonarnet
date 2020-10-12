package com.thilawfabrice.sonarnet.internal

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.thilawfabrice.sonarnet.ConnectionType
import com.thilawfabrice.sonarnet.ConnectivityCallback
import com.thilawfabrice.sonarnet.ConnectivityResult
import com.thilawfabrice.sonarnet.internal.interfaces.InternetReachabilityInterface
import com.thilawfabrice.sonarnet.internal.interfaces.NetworkInfoInterface

internal class NetworkStateWatcher private constructor(
    private val context: Context,
    private val resolveNetworkInfo: NetworkInfoInterface,
    private val reachability: InternetReachabilityInterface
) {

    @Volatile
    private var networkNotificationIsEnabled = false

    @Volatile
    private lateinit var callback: (result: ConnectivityResult) -> Unit

    private val cm: ConnectivityManager by lazy {
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
    }

    /**
     * Stars to listen to any network changes on device to check the current network the device is connected to.
     * And also verify whether you're offline.
     */
    fun startWatching(callback: (result: ConnectivityResult) -> Unit) {

        if (!networkNotificationIsEnabled) {
            val networkRequest = NetworkRequest.Builder().build()
            try {
                cm.registerNetworkCallback(networkRequest, internalNetworkCallback)
                this.callback = callback
                networkNotificationIsEnabled = true
            } catch (e: Exception) {
            }
        }

    }

    /**
     * Stop checking network changes on device
     */
    fun stopWatching() {
        networkNotificationIsEnabled = false
        cm.unregisterNetworkCallback(internalNetworkCallback)
    }

    private val internalNetworkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onLost(network: Network) {
            super.onLost(network)
            updateConnectivityResult()
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            updateConnectivityResult()
        }

        override fun onUnavailable() {
            super.onUnavailable()
            updateConnectivityResult()
        }


        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            updateConnectivityResult()
        }
    }

    private fun updateConnectivityResult() {
        if (networkNotificationIsEnabled) {
            provideConnectivityResult { result ->
                if (this::callback.isInitialized) {
                    callback(result)
                }
            }
        }
    }

    private fun provideConnectivityResult(callback: ConnectivityCallback) {

        /* network status */
        val connectionType =
            when {
                resolveNetworkInfo.networkIsCellular(cm) -> ConnectionType.Cellular
                resolveNetworkInfo.networkIsWifi(cm) -> ConnectionType.Wifi
                resolveNetworkInfo.networkIsEthernet(cm) -> ConnectionType.Ethernet
                else -> ConnectionType.Unknown
            }

        if (this::callback.isInitialized) {
            reachability.ping { internetAccess ->
                callback(ConnectivityResult(internetAccess, connectionType))
            }
        }
    }

    companion object {

        @Volatile
        private var instance: NetworkStateWatcher? = null
        fun with(
            context: Context, resolveNetworkInfo: NetworkInfoInterface,
            reachability: InternetReachabilityInterface
        ): NetworkStateWatcher {
            synchronized(this) {
                return instance ?: NetworkStateWatcher(
                    context, resolveNetworkInfo, reachability
                ).also {
                    instance = it
                }
            }
        }
    }

}