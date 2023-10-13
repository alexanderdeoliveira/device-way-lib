package com.waydatasolution.devicewaylib.domain

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.waydatasolution.devicewaylib.BluetoothReader
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.SamplesPeriod

internal class ReadDataUseCaseImpl: ReadDataUseCase {
    override suspend fun invoke(
        context: Context,
        bluetoothAdapter: BluetoothAdapter,
        device: BluetoothDevice,
        onFinished: () -> Unit
    ) {
        BluetoothReader(context, bluetoothAdapter).apply {
            readBluetoothSamples(device, SamplesPeriod.READ_ALL_SAMPLES) { list, success, isFinished ->
                if (success) {
                    if (!list.isNullOrEmpty()) {
                        device.samples.addAll(list)
                    }
                }
                if (isFinished) {
                    onFinished.invoke()
                }
            }
        }
    }
}