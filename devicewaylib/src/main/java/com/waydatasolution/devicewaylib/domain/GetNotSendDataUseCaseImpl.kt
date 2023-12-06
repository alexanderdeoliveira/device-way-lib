package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.NotSendData
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class GetNotSendDataUseCaseImpl(
    private val repository: DeviceWayRepository
): GetNotSendDataUseCase {
    override suspend fun invoke(): List<NotSendData> {
        return repository.getNotSendData()
    }
}