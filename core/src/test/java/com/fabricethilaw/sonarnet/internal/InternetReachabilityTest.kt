package com.fabricethilaw.sonarnet.internal

import com.fabricethilaw.sonarnet.InternetStatus
import junit.framework.TestCase
import org.junit.Test
import kotlin.random.Random

class InternetReachabilityTest : TestCase() {

    @Test
    fun connectivity_check_url_is_correct() {
        assertEquals(
            InternetReachability.GOOGLE_CONNECTIVITY_CHECK_URL,
            "https://connectivitycheck.gstatic.com/generate_204"
        )
    }

    @Test
    fun test_probe_result_result_internet() {
        val internetReachability = InternetReachability()
        val responseLength = 0
        val httpCode = 204

        assertEquals(
            internetReachability.testProbeResult(
                responseLength,
                httpCode
            ),
            InternetStatus.INTERNET
        )
    }

    @Test
    fun test_probe_result_wrong_http_code() {
        val internetReachability = InternetReachability()
        val responseLength = 0
        val httpCode = Random.nextInt(100, 203)

        assertEquals(
            internetReachability.testProbeResult(
                responseLength,
                httpCode
            ),
            InternetStatus.CAPTIVE_PORTAL
        )
    }

    @Test
    fun test_probe_result_wrong_http_code_2() {
        val internetReachability = InternetReachability()
        val responseLength = 0
        val httpCode = Random.nextInt(205, 599)

        assertEquals(
            internetReachability.testProbeResult(
                responseLength,
                httpCode
            ),
            InternetStatus.CAPTIVE_PORTAL
        )
    }

    @Test
    fun test_probe_result_wrong_http_response_length() {
        val internetReachability = InternetReachability()
        val responseLength = Random.nextInt(1, Int.MAX_VALUE)
        val httpCode = 204

        assertEquals(
            internetReachability.testProbeResult(
                responseLength,
                httpCode
            ),
            InternetStatus.CAPTIVE_PORTAL
        )
    }
}