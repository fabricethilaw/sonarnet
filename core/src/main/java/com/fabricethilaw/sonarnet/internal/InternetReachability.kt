package com.fabricethilaw.sonarnet.internal


import android.util.Log
import com.fabricethilaw.sonarnet.InternetStatus
import com.fabricethilaw.sonarnet.InternetStatus.*
import com.fabricethilaw.sonarnet.InternetStatusCallback
import com.fabricethilaw.sonarnet.internal.interfaces.InternetReachabilityInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 *  Implementation that provides the actual availability of Internet on device
 */
internal class InternetReachability : InternetReachabilityInterface {

    override fun ping(callback: InternetStatusCallback) {
        CoroutineScope(IO).launch {
            callback.invoke(makeProbeRequest())
        }

    }

    override suspend fun ping(): InternetStatus {
        return (makeProbeRequest())

    }


    private suspend fun makeProbeRequest(): InternetStatus {
        val url = GOOGLE_CONNECTIVITY_CHECK_URL
        return withContext(IO) {
            try {
                val httpConnection = if (url.startsWith("https", true)) {
                    URL(url).openConnection() as HttpsURLConnection
                } else URL(url).openConnection() as HttpURLConnection
                httpConnection.requestMethod = "GET"
                httpConnection.connectTimeout = 2000
                httpConnection.useCaches = false
                httpConnection.setRequestProperty("Content-Type", "application/json; utf-8")
                httpConnection.connect()
                val responseLength: Int = httpConnection.contentLength
                val responseCode = httpConnection.responseCode //this is http(s) response code
                Log.i("Sonarnet", "Response Code : $responseCode")
                httpConnection.disconnect()
                resolveProbeResult(responseLength, responseCode)
            } catch (e: java.lang.Exception) {
                NO_INTERNET
            }

        }
    }

    @TestOnly()
    fun testProbeResult(responseContentLength: Int, httpCode: Int): InternetStatus {
        return resolveProbeResult(responseContentLength, httpCode)
    }

    private fun resolveProbeResult(
        responseLength: Int,
        httpCode: Int
    ): InternetStatus {
        // GOOGLE_CONNECTIVITY_CHECK_URL is expected to return HTTP 204
        // with no content as a response
        return if (httpCode == 204 && responseLength == 0
        ) {
            INTERNET
        } else {
            // the request would have been intercepted
            CAPTIVE_PORTAL
        }
    }

    companion object {
        const val GOOGLE_CONNECTIVITY_CHECK_URL =
            "https://connectivitycheck.gstatic.com/generate_204"
    }
}