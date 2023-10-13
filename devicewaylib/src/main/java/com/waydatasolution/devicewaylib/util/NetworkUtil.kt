package com.waydatasolution.devicewaylib.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build

internal object NetworkUtil {
    fun internetIsActivated(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities != null
        } else {
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            activeNetwork != null && activeNetwork.isConnected
        }
    }
}