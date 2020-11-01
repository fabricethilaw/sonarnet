package com.fabricethilaw.sonarnet.internal


import android.util.Log
import com.fabricethilaw.sonarnet.InternetStatus.*
import com.fabricethilaw.sonarnet.InternetStatusCallback
import com.fabricethilaw.sonarnet.internal.interfaces.InternetReachabilityInterface
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection.HTTP_NO_CONTENT

/**
 *  Implementation that provides the actual availability of Internet on device
 */
internal class InternetReachability : InternetReachabilityInterface {

    override fun ping(callback: InternetStatusCallback) {
        val url = GOOGLE_CONNECTIVITY_CHECK_URL
        makeProbeRequest(url, callback)
    }

    /**
     * Send a simple http request to a url. The response is checked and returned as [InternetStatus]
     *
     */
    private fun makeProbeRequest(url: String, callback: InternetStatusCallback) {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("content-type", "application/json")
            .addHeader("cache-control", "no-cache")
            .build()

        // The Google connectivity check url
        // returns HTTP code 204
        val expectedHttpCode: Int = HTTP_NO_CONTENT

        try {
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(NO_INTERNET)
                    Log.e("INTERNET STATUS", "FAILURE, ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val internetStatus =
                        if (response.isSuccessful && response.code == expectedHttpCode) {
                            INTERNET
                        } else {
                            CAPTIVE_PORTAL // connection might be intercepted
                        }

                    Log.e("INTERNET STATUS", "Http code: ${response.code}")
                    callback(internetStatus)
                }
            })
        } catch (e: Exception) {
            // we just catch any exception that may occurs
        }
    }


    private val GOOGLE_CONNECTIVITY_CHECK_URL = "https://connectivitycheck.gstatic.com/generate_204"
}