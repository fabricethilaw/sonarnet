# SonarNet


[![](https://jitpack.io/v/fabricethilaw/sonarnet.svg)](https://jitpack.io/#fabricethilaw/sonarnet) 
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/99c6454342b44241b7b2abb6a70647b0)](https://app.codacy.com/gh/fabricethilaw/sonarnet?utm_source=github.com&utm_medium=referral&utm_content=fabricethilaw/sonarnet&utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/8c44053197903e4669af/maintainability)](https://codeclimate.com/github/fabricethilaw/sonarnet/maintainability)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

An open-source Android library providing a better implementation of Internet access and captive portals detection in your apps.

<img src="https://github.com/fabricethilaw/sonarnet/blob/master/showcase.png" width="750" />

## Technology stack

- SonarNet is primarily built in Kotlin

- Compatible with Android 5.0+ (API level >= 21)

## Features

- [x] Detect when device has joined a network that has no Internet access.
- [x] Detect when connected to a router with captive portal

## How it works 

SonarNet wraps the ConnectivityManager and lets your app detect true Internet access, not just if the device has joinded a network. So when ConnectivityManager detects Wi-Fi or Cellular network, SonarNet uses a tiny HTTP probe to a known URL (such as `connectivitycheck.gstatic.com`), to detect whether there is true Internet access, or whether a captive portal is preventing the device to access Internet.

## Add Sonarnet to your project [![](https://jitpack.io/v/fabricethilaw/sonarnet.svg)](https://jitpack.io/#fabricethilaw/sonarnet) 

Step 1: Add in your root `build.gradle` at the end of repositories:

```gradle
   allprojects {
    repositories {
       maven { url 'https://jitpack.io' }
    }
   }
 
 ```
 
 or if there is (the new) `dependencyResolutionManagement` in settings.gradle :
 
```gradle
   dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url 'https://jitpack.io' }
    }
  }
```

Step 2: Add the dependency
 
 ```gradle
    dependencies {
      implementation 'com.github.fabricethilaw:sonarnet:1.0.0'
    }
```

## Check Internet status

Usable in any class.

```kotlin
// Detect that INTERNET is available. Get the result from a callback
SonarNet.ping { result ->
    // check result
    when(result) {
        InternetStatus.INTERNET -> {}
        InternetStatus.NO_INTERNET -> {}
        InternetStatus.CAPTIVE_PORTAL -> {}
     }
}
```

You can also call Ping as a suspending function :
```kotlin
val internetStatus: InternetStatus = SonarNet.ping()
if(internetStatus == INTERNET) {
  // Do something
} else {
  // Proceed otherwise
}

```

Here is a idiom that enables to perform an action only if internet is available:
```kotlin
SonarNet.runWithInternet {
           // block of logic
        }
 ```


**Note**: In order to perform network operations, the following permissions must be added into your application `AndroidManifest.xml` :

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

```

## Notifications

If you prefer receiving notifications about changes in connectivity , you would provide a context ( from an Activity or a Fragment ) and register a `ConnectivityCallback` .

A notification from ConnectivityCallback provides knowledge about `InternetStatus` and the connected `NetworkType`

```kotlin
// Set a connectivity callback
 val connectivityCallback = object : ConnectivityCallback {
      override fun onConnectionChanged(result: ConnectivityResult) {
          // Check the result, see the Using Results section
         }
     }

  // register the callback
 SonarNet(context).registerConnectivityCallback(connectivityCallback)
 
```

When you no longer want to receive updates on connectivity events :

```kotlin
// unregister the callback
 SonarNet(context).unregisterConnectivityCallback()

```

## Using results

`ConnectivityResult` is provided in network notification callbacks. It has a few useful fields :

- `internetStatus` value can be one of : `INTERNET`, `NO_INTERNET`, `CAPTIVE_PORTAL`

- `networkType` value can be one of : `Cellular`, `Wifi`, `Ethernet`, `Unknown`

## Network types

You may also use the following methods if you are only interested in checking the type of connected network :

``connectedViaWiFi()``, ``connectedViaCellular()``, ``connectedViaEthernet()``

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

You have checked this library out.

- Did you find a bug ? 
- Did you write a patch that fixes a bug?
- Do you intend to add a new feature or change an existing one?

We encourage you to read the instructions on how to contribute, stated in [CONTRIBUTING](CONTRIBUTING.md).
