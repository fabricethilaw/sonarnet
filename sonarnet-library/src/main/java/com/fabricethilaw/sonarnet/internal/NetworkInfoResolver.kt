package com.fabricethilaw.sonarnet.internal

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.fabricethilaw.sonarnet.NetworkType
import com.fabricethilaw.sonarnet.internal.interfaces.NetworkInfoInterface

/**
 *
 */
internal class NetworkInfoResolver : NetworkInfoInterface {

    override fun networkIsCellular(connectivityManager: ConnectivityManager): Boolean {
        return isMobileType(connectivityManager)
    }

    override fun networkIsWifi(connectivityManager: ConnectivityManager): Boolean {
        return isWifiType(connectivityManager)
    }

    override fun networkIsEthernet(connectivityManager: ConnectivityManager): Boolean {
        return isEthernet(connectivityManager)
    }

    private fun isMobileType(connectivityManager: ConnectivityManager): Boolean {
        return checkNetworkType(connectivityManager, NetworkType.Cellular)
    }

    private fun isWifiType(connectivityManager: ConnectivityManager): Boolean {
        return checkNetworkType(connectivityManager, NetworkType.Wifi)
    }

    private fun isEthernet(connectivityManager: ConnectivityManager): Boolean {
        return checkNetworkType(connectivityManager, NetworkType.Ethernet)
    }

    private fun checkNetworkType(
        connectivityManager: ConnectivityManager,
        network: NetworkType
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                .also { connectivity ->
                    return connectivity != null && connectivity.hasTransport(
                        resolveNetworkTransport(network)
                    )
                }
        } else {
            connectivityManager.getNetworkInfo(resolveDataConnection(network))
                .also { connectionStatus ->
                    return connectionStatus != null && connectionStatus.isConnected
                }
        }
    }

    private fun resolveNetworkTransport(type: NetworkType): Int {
        return when (type) {
            NetworkType.Cellular -> NetworkCapabilities.TRANSPORT_CELLULAR
            NetworkType.Wifi -> NetworkCapabilities.TRANSPORT_WIFI
            NetworkType.Ethernet -> NetworkCapabilities.TRANSPORT_ETHERNET
            NetworkType.Unknown -> -1
        }
    }

    private fun resolveDataConnection(type: NetworkType): Int {
        return when (type) {
            NetworkType.Cellular -> ConnectivityManager.TYPE_MOBILE
            NetworkType.Wifi -> ConnectivityManager.TYPE_WIFI
            NetworkType.Ethernet -> ConnectivityManager.TYPE_ETHERNET
            NetworkType.Unknown -> -1
        }
    }
}