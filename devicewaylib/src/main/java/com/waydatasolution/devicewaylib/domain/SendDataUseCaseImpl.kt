package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.ResponseStatus
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository
import java.lang.Exception

internal class SendDataUseCaseImpl(
    private val repository: DeviceWayRepository
): SendDataUseCase {
    override suspend fun invoke(): ResponseStatus {
        return try {
            repository.sendData()
        } catch (e: Exception) {
            ResponseStatus.Failure(e.hashCode(), e.message ?: "Unknown exception")
        }
    }
}