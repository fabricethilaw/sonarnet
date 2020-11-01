# SonarNet

An open-source Android library providing a reliable, easy-to-use way to detect Internet access and captive portals on connected devices.

## Technology stack

- SonarNet is primarily built in Kotlin

- Inckudes a third-party dependency : [OkHttp](https://github.com/square/okhttp),

- Unit tests for the core functionalities are coming very soon.

## Features

- [x] Detect when device has joined a network that has no Internet access.
- [x] Detect when connected to a router with captive portal
- [x] Be notified about changes in Internet connectivity.

## How it works

*The classic ConnectivityManager 's methods tells whether there is a network interface capable to *allow for Intrernet access*. This is not enough to make sure of true Internet access when connected to a network. SonarNet's aim to solve this.

SonarNet wraps the ConnectivityManager and seeks to let your app replicate the Android OS means of detecting Internet and captive portals. So, when ConnectivityManager detects Wi-Fi or Cellular network, SonarNet uses a cleartext HTTP probe to a known URL (such as `connectivitycheck.gstatic.com`), to detect whether there is true Internet access, or whether a captive portal is intercepting the connections.

# Usage

Add this to your module's `build.gradle` file:

```gradle
dependencies {

  implementation 'com.fabricethilaw.sonarnet:core:0.0.2'
}
```

## Check Internet status

SonarNet allows for Internet connectivity verification within any class.

```
// Detect that INTERNET is available, sending the result to a callback
SonarNet.ping { result ->
    // Check the result, for further actions
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

- `internetStatus` field can be one of : `INTERNET`, `NO_INTERNET`, `CAPTIVE_PORTAL`

- `networkType` field can be one of : `Cellular`, `Wifi`, `Ethernet`, `Unknown`
