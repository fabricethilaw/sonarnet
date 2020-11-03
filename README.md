# SonarNet

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/99c6454342b44241b7b2abb6a70647b0)](https://app.codacy.com/gh/fabricethilaw/sonarnet?utm_source=github.com&utm_medium=referral&utm_content=fabricethilaw/sonarnet&utm_campaign=Badge_Grade)
[ ![Download](https://api.bintray.com/packages/fabricethilaw/sonarnet/sonarnet/images/download.svg?version=0.0.2) ](https://bintray.com/fabricethilaw/sonarnet/sonarnet/0.0.2/link)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

An open-source Android library providing a better implementation of Internet access and captive portals detection in your apps.

<img src="https://github.com/fabricethilaw/sonarnet/blob/master/showcase.png" width="750" />

## Technology stack

- SonarNet is primarily built in Kotlin

- Includes a third-party dependency : [OkHttp](https://github.com/square/okhttp),

- Unit tests coming very soon.

## Features

- [x] Detect when device has joined a network that has no Internet access.
- [x] Detect when connected to a router with captive portal
- [x] Be notified about changes in Internet connectivity.

## How it works

The classic ConnectivityManager's methods tell whether there is a network interface capable to *allow for Intrernet access*. But generally this is not enough to make sure of true Internet access when connected to a network. SonarNet's goal is to solve this.

SonarNet wraps the ConnectivityManager and seeks to let your app replicate the Android OS means of detecting Internet network. So, when ConnectivityManager detects Wi-Fi or Cellular network, SonarNet uses a cleartext HTTP probe to a known URL (such as `connectivitycheck.gstatic.com`), to detect whether there is true Internet access, or whether a captive portal is intercepting the connections.

# Usage

Add this to your module's `build.gradle` file:

```gradle
dependencies {
  
  implementation 'com.fabricethilaw.sonarnet:core:0.0.3'
}
```

## Check Internet status

Usable in any class.

```
// Detect that INTERNET is available
SonarNet.ping { result ->
    // check result
    when(result) {
        InternetStatus.INTERNET -> {}
        InternetStatus.NO_INTERNET -> {}
        InternetStatus.CAPTIVE_PORTAL -> {}
        }
}
```

**Note**: In order to perform network operations, the following permissions must be added into your application `AndroidManifest.xml` :

```<uses-permission
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Notifications

If you prefer receiving notifications about changes in connectivity , you would provide a context ( from an Activity or a Fragment ) and register a `ConnectivityCallback` .

A notification from ConnectivityCallback provides knowledge about `InternetStatus` and the connected `NetworkType`

```
// Set a connectivity callback
 val connectivityCallback = object : ConnectivityCallback {
      override fun onConnectionChanged(result: ConnectivityResult) {
          // Check the result, see the Using Results section
         }
     }

  // register the callback
 SonarNet.with(context).registerConnectivityCallback(connectivityCallback)
```

When you no longer want to receive updates on connectivity events :

```
// unregister the callback
 SonarNet.with(context).unregisterConnectivityCallback()
```

## Using results

`ConnectivityResult` is provided in network notification callbacks. It has a few useful fields :

- `internetStatus` value can be one of : `INTERNET`, `NO_INTERNET`, `CAPTIVE_PORTAL`

- `networkType` value can be one of : `Cellular`, `Wifi`, `Ethernet`, `Unknown`

## Network types

You may also use the following methods if you are only interested in some types of networks :

``connectedViaWiFi()``, ``connectedViaCellular()``, ``connectedViaEthernet()``
