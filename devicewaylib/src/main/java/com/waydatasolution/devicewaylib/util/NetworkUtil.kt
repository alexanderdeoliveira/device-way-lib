package com.waydatasolution.devicewaylib.util

import android.content.Context
import android.net.ConnectivityManager

internal object NetworkUtil {
    fun internetIsActivated(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return  capabilities != null
    }
}