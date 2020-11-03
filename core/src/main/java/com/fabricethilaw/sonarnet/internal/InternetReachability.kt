package com.fabricethilaw.sonarnet.internal


import android.util.Log
import com.fabricethilaw.sonarnet.InternetStatus
import com.fabricethilaw.sonarnet.InternetStatus.*
import com.fabricethilaw.sonarnet.InternetStatusCallback
import com.fabricethilaw.sonarnet.internal.interfaces.InternetReachabilityInterface
import okhttp3.*
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


    private fun makeProbeRequest(url: String, callback: InternetStatusCallback) {
        val client = OkHttpClient()

        val request: Request = buildRequest(url)

        // The Google connectivity check url
        // returns HTTP code 204
        val expectedHttpCode: Int = HTTP_NO_CONTENT

        try {
            client.newCall(request).enqueue(requestCallback(callback, expectedHttpCode))
        } catch (e: Exception) {
            // we just catch any exception that may occurs
        }
    }

    private fun buildRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .get()
            .addHeader("content-type", "application/json")
            .addHeader("cache-control", "no-cache")
            .build()
    }

    private fun resolveProbeResult(
        response: Response,
        expectedHttpCode: Int
    ): InternetStatus {
        return if (response.isSuccessful && response.code == expectedHttpCode) {
            INTERNET
        } else {
            CAPTIVE_PORTAL // connection might be intercepted
        }
    }

    private fun requestCallback(
        internetCallback: InternetStatusCallback,
        expectedHttpCode: Int
    ): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                internetCallback(NO_INTERNET)
                Log.e("INTERNET STATUS", "FAILURE, ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val internetStatus =
                    resolveProbeResult(response, expectedHttpCode)

                Log.e("INTERNET STATUS", "Http code: ${response.code}")
                internetCallback(internetStatus)
            }
        }
    }

    private val GOOGLE_CONNECTIVITY_CHECK_URL = "https://connectivitycheck.gstatic.com/generate_204"
}