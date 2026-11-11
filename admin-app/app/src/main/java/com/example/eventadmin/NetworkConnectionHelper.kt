package com.example.eventadmin

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkConnectionHelper(context: Context): LiveData<Boolean>() {
    private val cm = context.applicationContext
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetwork = mutableSetOf<Network>()

    private val networkCallback = object: ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            if (hasValidatedInternet(capabilities))
                validNetwork.add(network)
            else
                validNetwork.remove(network)
            postValue(validNetwork.isNotEmpty())
        }

        override fun onLost(network: Network) {
            validNetwork.remove(network)
            postValue(validNetwork.isNotEmpty())
        }
    }
    override fun onActive() {
        super.onActive()
        validNetwork.clear()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(networkRequest, networkCallback)
        checkInitialConnection()
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
    }
    private fun hasValidatedInternet(capabilities: NetworkCapabilities) : Boolean =
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    private fun checkInitialConnection() {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        postValue(capabilities != null && hasValidatedInternet(capabilities))
    }
}