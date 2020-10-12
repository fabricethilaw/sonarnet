/**
 * Designed and developed by Thilaw Fabrice (@fabricethilaw)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thilawfabrice.samplesonarnet

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.thilawfabrice.sonarnet.ConnectivityResult
import com.thilawfabrice.sonarnet.SonarNet

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_main, container, false)
        val btnCheckConnectionStatus = mView.findViewById<Button>(R.id.check)
        val btnToggleLiveNetworkChecking =
            mView.findViewById<Button>(R.id.toggleLiveChecking)

        val dialog = AlertDialog.Builder(this.requireContext())
        dialog.create()

        btnCheckConnectionStatus.setOnClickListener {
            checkInternet(dialog)
        }

        btnToggleLiveNetworkChecking.setOnClickListener {
            toggleLiveConnectivity(btnToggleLiveNetworkChecking)
        }
        return mView
    }


    private fun checkInternet(dialog: AlertDialog.Builder) {
        SonarNet.ping { result ->
            val message = "Device is $result"

            Handler(Looper.getMainLooper()).post { dialog.setMessage(message).show() }
        }
    }

    private fun showConnectivityState(state: ConnectivityResult) {
        Handler(Looper.getMainLooper()).post {
            val message =
                "Device is ${state.internet} over ${state.connectionType} network"
            Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun toggleLiveConnectivity(toggleButton: Button) {
        toggleButton.text.let {
            if (it == getString(R.string.start)) {
                // Enable live checking and attach observer
                SonarNet.startObservingConnectivity(requireContext()) { result ->
                    showConnectivityState(result)
                }
                // update button 's text
                toggleButton.text = getString(R.string.stop)
            } else {
                // Disable live checking
                SonarNet.stopObservingConnectivity()
                // update button 's text
                toggleButton.text = getString(R.string.start)

            }
        }
    }

}