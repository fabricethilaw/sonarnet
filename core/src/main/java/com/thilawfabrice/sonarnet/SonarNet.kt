package com.thilawfabrice.sonarnet

import android.content.Context
import com.thilawfabrice.sonarnet.internal.InternetReachability
import com.thilawfabrice.sonarnet.internal.NetworkInfoResolver
import com.thilawfabrice.sonarnet.internal.NetworkStateWatcher

class SonarNet {


    companion object {

        /**
         * Performs a tiny http request to a remote destination, to check true internet Access,
         * and invoking the [InternetAccessCallback] with the result.
         */
        fun ping(internetAccessCallback: InternetAccessCallback) {
            reachability.ping(internetAccessCallback)
        }

        /**
         * Starts listening to any network changes (including internet access),
         * and invoking the [ConnectivityCallback] with the result.
         */
        fun startObservingConnectivity(
            context: Context,
            connectivityCallback: ConnectivityCallback
        ) {
            if (!this::watcher.isInitialized) {
                watcher = NetworkStateWatcher.with(context, networkInfo, reachability)
            }
            watcher.startWatching(connectivityCallback)
        }

        /**
         * Stop listening to network changes.
         */
        fun stopObservingConnectivity() {
            if (this::watcher.isInitialized) {
                watcher.stopWatching()
            }
        }

        private val networkInfo by lazy { NetworkInfoResolver() }
        private val reachability by lazy { InternetReachability() }
        private lateinit var watcher: NetworkStateWatcher
    }
}