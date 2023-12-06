package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Sample
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class SaveDataUseCaseImpl(
    private val repository: DeviceWayRepository
): SaveDataUseCase {
    override suspend fun invoke(
        device: BluetoothDevice,
        onFinished: () -> Unit
    ) {
        repository.saveData(
            device,
            onFinished
        )
    }
}