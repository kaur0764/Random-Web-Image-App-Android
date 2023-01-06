package com.example.mad9132_a1

/*
 * Completed by Jasreet Kaur on November 19, 2022
 */

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class InternetConnection(private val context: Context) {

    // region Properties

    val isConnected: Boolean
        get() = checkNetworkConnectivity()

    // endregion

    // region Methods

    private fun checkNetworkConnectivity(): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)

        return (connection?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false || connection?.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        ) ?: false)

    }

    // endregion

}