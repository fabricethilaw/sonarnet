package com.fabricethilaw.sonarnet

import android.content.Context
import com.fabricethilaw.sonarnet.internal.InternetReachability
import com.fabricethilaw.sonarnet.internal.NetworkInfoResolver
import com.fabricethilaw.sonarnet.internal.NetworkStateWatcher

/**
 *
 */
class SonarNet(private val context: Context) {


    /**
     * Returns true if active connection is WiFi
     */
    fun connectedViaWiFi(): Boolean {
        return networkStateWatcher.connectedViaWiFi()
    }

    /**
     * Return true if active connection is Cellular (a.k.a Mobile)
     */
    fun connectedViaCellular(): Boolean {
        return networkStateWatcher.connectedViaCellular()
    }

    /**
     * Return true if active connection is Ethernet
     */
    fun connectedViaEthernet(): Boolean {
        return networkStateWatcher.connectedViaEthernet()
    }


    /**
     * Registers to changes in Internet connectivity and network type.
     * The callback will continue to receive notifications until [unregisterConnectivityCallback]
     * is called.
     */
    fun registerConnectivityCallback(
        callback: ConnectivityCallback
    ) {
        networkStateWatcher.startWatching(callback)
    }


    /**
     * Stops receiving notifications of connectivity changes.
     */
    fun unregisterConnectivityCallback() {
        networkStateWatcher.stopWatching()
    }


    private val networkStateWatcher by lazy {
        NetworkStateWatcher.with(
            context,
            networkInfoResolver,
            internetReachability
        )
    }


    companion object {

        /**
         * Uses a cleartext Http probe to reach a known destination ([like this one](https://connectivitycheck.gstatic.com/generate_204)),
         * and invoking the [InternetStatusCallback] with the result.
         */
        fun ping(internetStatusCallback: InternetStatusCallback) {
            internetReachability.ping(internetStatusCallback)
        }

        private val networkInfoResolver by lazy { NetworkInfoResolver() }

        private val internetReachability by lazy { InternetReachability() }


        @Volatile
        private var instance: SonarNet? = null
        fun with(
            context: Context
        ): SonarNet {
            synchronized(this) {
                return instance ?: SonarNet(context).also {
                    instance = it
                }
            }
        }
    }
}