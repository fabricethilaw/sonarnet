package com.thilawfabrice.sonarnet.internal


import android.util.Log
import com.thilawfabrice.sonarnet.InternetAccess.*
import com.thilawfabrice.sonarnet.InternetAccessCallback
import com.thilawfabrice.sonarnet.internal.interfaces.InternetReachabilityInterface
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection.HTTP_NO_CONTENT

/**
 *  Implementation that provides the actual availability of Internet on device
 */
internal class InternetReachability :
    InternetReachabilityInterface {


    override fun ping(callback: InternetAccessCallback) {
        val url = GOOGLE_CONNECTIVITY_CHECK_URL
        pingConnectivityCheckDestination(url, callback)
    }

    /**
     * Given a reliable destination url, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful and it matches the expected http code or body response,
     * then device is ONLINE, otherwise chances are you got redirected to the CAPTIVE portal.
     * If request fails then your are not certainly OFFLINE.
     *
     */
    private fun pingConnectivityCheckDestination(url: String, callback: InternetAccessCallback) {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url("http://connectivitycheck.gstatic.com/generate_204")
            .get()
            .addHeader("content-type", "application/json")
            .addHeader("cache-control", "no-cache")
            .build()

        // The Google Chrome connectivity check url
        // returns HTTP code 204
        val expectedHttpCode: Int = HTTP_NO_CONTENT

        try {
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(OFFLINE)
                    Log.e("INTERNET CHECK", "FAILURE, ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val internetAccess =
                        if (response.isSuccessful && response.code == expectedHttpCode
                        //  && response.body.toString() == expectedContent
                        ) {
                            ONLINE
                        } else {
                            CAPTIVE
                        }
                    Log.e("INTERNET CHECK", "Http code: ${response.code}")
                    callback(internetAccess)
                }
            })
        } catch (e: Exception) {
            // we just catch any exception that may occurs
        }
    }



    private val GOOGLE_CONNECTIVITY_CHECK_URL = "https://connectivitycheck.gstatic.com/generate_204"
}