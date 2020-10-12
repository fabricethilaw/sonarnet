package com.thilawfabrice.sonarnet

enum class ConnectionType {
    Cellular,
    Wifi,
    Ethernet,
    Unknown
}

enum class InternetAccess {
    ONLINE, OFFLINE, CAPTIVE
}