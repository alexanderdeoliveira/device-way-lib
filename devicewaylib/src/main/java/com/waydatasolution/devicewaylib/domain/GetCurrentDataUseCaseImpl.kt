package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class GetCurrentDataUseCaseImpl(
    private val repository: DeviceWayRepository
): GetCurrentDataUseCase {
    override suspend fun invoke(
        sensorId: String
    ): List<Data>? {
        return repository.getCurrentData(sensorId)
    }
}