package com.fabricethilaw.sonarnet.internal.interfaces

import com.fabricethilaw.sonarnet.InternetStatusCallback

/**
 *
 */
internal interface InternetReachabilityInterface {
    /**
     * Sends a cookie-less request to a fast and reliable online resource,
     * in order to determine detect if device is ONLINE, OFFLINE or whether the network is CAPTIVE.
     * That's [what the Google Chrome app does too](https://www.google.com/chrome/privacy/whitepaper.html).
     *
     * You shouldn't use native Android framework method "ConnectivityManager.getActiveNetworkInfo()
     * to check internet availability. It only verifies if a network connection has been made,
     * but does not check that device is connected to the internet.
     */
    fun ping(callback: InternetStatusCallback)
}