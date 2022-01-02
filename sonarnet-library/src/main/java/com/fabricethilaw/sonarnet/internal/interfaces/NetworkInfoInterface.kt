package com.fabricethilaw.sonarnet.internal.interfaces

import android.net.ConnectivityManager

/**
 *
 */
internal interface NetworkInfoInterface {
    /**
     *
     */
    fun networkIsCellular(connectivityManager: ConnectivityManager): Boolean

    /**
     *
     */
    fun networkIsWifi(connectivityManager: ConnectivityManager): Boolean

    /**
     *
     */
    fun networkIsEthernet(connectivityManager: ConnectivityManager): Boolean

}