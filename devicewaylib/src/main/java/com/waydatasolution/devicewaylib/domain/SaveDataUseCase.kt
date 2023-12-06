package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Sample

internal interface SaveDataUseCase {
    suspend operator fun invoke(
        device: BluetoothDevice,
        onFinished: () -> Unit
    )
}