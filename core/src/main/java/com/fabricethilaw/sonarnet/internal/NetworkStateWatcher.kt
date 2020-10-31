package com.fabricethilaw.sonarnet.internal

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.fabricethilaw.sonarnet.ConnectivityCallback
import com.fabricethilaw.sonarnet.ConnectivityResult
import com.fabricethilaw.sonarnet.NetworkType
import com.fabricethilaw.sonarnet.internal.interfaces.InternetReachabilityInterface
import com.fabricethilaw.sonarnet.internal.interfaces.NetworkInfoInterface

/**
 *
 */
internal class NetworkStateWatcher private constructor(
    private val context: Context,
    private val resolveNetworkInfo: NetworkInfoInterface,
    private val reachability: InternetReachabilityInterface
) {

    @Volatile
    private var networkNotificationIsEnabled = false

    @Volatile
    private lateinit var callback: ConnectivityCallback

    private val cm: ConnectivityManager by lazy {
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
    }

    /**
     * Stars to listen to any network changes on device to check the current network the device is connected to.
     * And also verify whether you're offline.
     */
    fun startWatching(callback: ConnectivityCallback) {

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
     * Returns true if active network is WiFi
     */
    fun connectedViaWiFi(): Boolean {
        return resolveNetworkInfo.networkIsWifi(cm)
    }

    /**
     * Return true if active network is cellular
     */
    fun connectedViaCellular(): Boolean {
        return resolveNetworkInfo.networkIsCellular(cm)
    }

    /**
     * Return true if active network is cellular
     */
    fun connectedViaEthernet(): Boolean {
        return resolveNetworkInfo.networkIsEthernet(cm)
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

            if (this::callback.isInitialized) {
                provideConnectivityResult(callback)
            }

        }
    }

    private fun provideConnectivityResult(callback: ConnectivityCallback) {

        /* network status */
        val connectionType =
            getConnectionType()

        if (this::callback.isInitialized) {

            reachability.ping { internetAccess ->
                callback.onConnectionChanged(ConnectivityResult(internetAccess, connectionType))
            }
        }
    }

    private fun getConnectionType(): NetworkType {
        return when {
            resolveNetworkInfo.networkIsCellular(cm) -> NetworkType.Cellular
            resolveNetworkInfo.networkIsWifi(cm) -> NetworkType.Wifi
            resolveNetworkInfo.networkIsEthernet(cm) -> NetworkType.Ethernet
            else -> NetworkType.Unknown
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