package com.fabricethilaw.sonarnet.internal

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                .also { connectivity ->
                    return connectivity != null && connectivity.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                }
        } else {
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .also { mobileNetwork ->
                    return mobileNetwork != null && mobileNetwork.isConnected
                }
        }
    }

    private fun isWifiType(connectivityManager: ConnectivityManager): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                .also { connectivity ->
                    return connectivity != null && connectivity.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }
        } else {
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .also { wifiNetwork ->
                    return wifiNetwork != null && wifiNetwork.isConnected
                }
        }

    }

    private fun isEthernet(connectivityManager: ConnectivityManager): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                .also { connectivity ->
                    return connectivity != null && connectivity.hasTransport(
                        NetworkCapabilities.TRANSPORT_ETHERNET
                    )
                }
        } else {
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
                .also { ethernetNetwork ->
                    return ethernetNetwork != null && ethernetNetwork.isConnected
                }
        }

    }

}