package com.kuwelym.koinvert.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = context?.let { isInternetAvailable(it) }
        if (isConnected == true) {
            LocalBroadcastManager.getInstance(context)
                .sendBroadcast(Intent(ACTION_INTERNET_AVAILABLE))
        }
    }

    companion object {
        const val ACTION_INTERNET_AVAILABLE = "com.kuwelym.koinvert.INTERNET_AVAILABLE"
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return networkCapabilities != null &&
            (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
}
