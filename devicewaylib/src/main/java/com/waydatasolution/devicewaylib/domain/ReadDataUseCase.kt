package com.waydatasolution.devicewaylib.domain

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice

internal interface ReadDataUseCase {
    suspend operator fun invoke(
        context: Context,
        bluetoothAdapter: BluetoothAdapter,
        device: BluetoothDevice,
        onFinished: () -> Unit
    )
}