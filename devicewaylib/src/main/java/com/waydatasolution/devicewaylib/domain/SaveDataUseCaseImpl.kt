package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.Sample
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class SaveDataUseCaseImpl(
    private val repository: DeviceWayRepository
): SaveDataUseCase {
    override suspend fun invoke(
        sensorId: String,
        samples: List<Sample>,
        onFinished: () -> Unit
    ) {
        repository.saveData(
            sensorId,
            samples,
            onFinished
        )
    }
}