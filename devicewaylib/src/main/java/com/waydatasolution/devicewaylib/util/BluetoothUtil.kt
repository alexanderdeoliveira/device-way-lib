package com.waydatasolution.devicewaylib.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter

internal object BluetoothUtil {
    @SuppressLint("MissingPermission")
    fun isBluetoothConnected(): Boolean {
         val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled
    }
}