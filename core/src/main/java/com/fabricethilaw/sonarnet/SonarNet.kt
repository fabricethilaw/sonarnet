package com.fabricethilaw.sonarnet

import android.content.Context
import android.content.ContextWrapper
import com.fabricethilaw.sonarnet.internal.InternetReachability
import com.fabricethilaw.sonarnet.internal.NetworkInfoResolver
import com.fabricethilaw.sonarnet.internal.NetworkStateWatcher

/**
 * Present an interface for making internet connectivity requests
 * with results such as [InternetStatus], [NetworkType] and [ConnectivityResult]
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
        NetworkStateWatcher(
            ContextWrapper(context.applicationContext),
            networkInfoResolver,
            internetReachability
        )
    }


    companion object {

        /**
         * A static method that uses a cleartext Http probe to reach a known lightweight destination,
         * and invoking the [InternetStatusCallback] with the result.
         * The process is a mere http request.
         */
        fun ping(internetStatusCallback: InternetStatusCallback) {
            internetReachability.ping(internetStatusCallback)
        }

        /**
         * A suspendable function that uses a cleartext Http probe to reach a known lightweight destination,
         * and directly returns the result.
         * The process is a mere http request.
         */
        suspend fun ping() = internetReachability.ping()

        private val networkInfoResolver by lazy { NetworkInfoResolver() }

        private val internetReachability by lazy { InternetReachability() }

        @Volatile
        private var instance: SonarNet? = null

        /**
         * Begin an internet connectivity check with SonarNet by passing in a context.
         * Any requests started using a context will only have the application level options applied
         * and will not be started or stopped based on lifecycle events.
         *
         */
        @Deprecated("Use SonarNet class constructor instead in order to avoid memory leak).", ReplaceWith("SonarNet(context)"))
        fun with(
            context: Context
        ): SonarNet {
            synchronized(this) {
                return instance ?: SonarNet(context).also {
                    instance = it
                }
            }
        }

        /**
         * Performs an action only if Internet is available
         */
        fun runWithInternet(action: () -> Unit) {
            ping {
                if (it == InternetStatus.INTERNET) action.invoke()
            }
        }

    }
}