package com.fabricethilaw.sonarnet.internal

import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.fabricethilaw.sonarnet.ConnectivityCallback
import com.fabricethilaw.sonarnet.ConnectivityResult
import com.fabricethilaw.sonarnet.NetworkType
import com.fabricethilaw.sonarnet.internal.interfaces.InternetReachabilityInterface
import com.fabricethilaw.sonarnet.internal.interfaces.NetworkInfoInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 *
 */
internal class NetworkStateWatcher(
    private val contextWrapper: ContextWrapper,
    private val resolveNetworkInfo: NetworkInfoInterface,
    private val reachability: InternetReachabilityInterface
) {

    @Volatile
    private var networkNotificationIsEnabled = false

    @Volatile
    private lateinit var callback: ConnectivityCallback

    private val job = CoroutineScope(Dispatchers.IO)

    private val networkStateFlow = MutableStateFlow(-1)

    private val cm: ConnectivityManager by lazy {
        contextWrapper.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
    }

    /**
     * Stars to listen to any network changes on device to check the current network the device is connected to.
     * And also verify whether you're offline.
     */
    fun startWatching(callback: ConnectivityCallback) {
        job.launch {
            networkStateFlow.collectLatest {
                if(it != -1) updateConnectivityResult()
            }
        }

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
            job.launch {
                networkStateFlow.emit(0)
            }
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            job.launch {
                networkStateFlow.emit(1)
            }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            job.launch {
                networkStateFlow.emit(2)
            }
        }


        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            job.launch {
                networkStateFlow.emit(3)
            }
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

}