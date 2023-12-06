package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class ClearDatabaseUseCaseImpl(
    private val repository: DeviceWayRepository
): ClearDatabaseUseCase {
    override suspend fun invoke() {
        repository.clearDatabase()
    }
}