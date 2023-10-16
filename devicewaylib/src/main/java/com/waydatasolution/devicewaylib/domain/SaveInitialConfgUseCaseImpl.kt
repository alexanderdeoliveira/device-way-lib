package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.InitialConfig
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class SaveInitialConfgUseCaseImpl(
    private val repository: DeviceWayRepository
): SaveInitialConfigUseCase {
    override suspend fun invoke(
        authToken: String,
        estabCode: Int,
        routeCode: Int,
        driverCode: Int,
        sensorCode: Int,
        onFinished: () -> Unit
    ) {
        repository.saveInitialConfig(
            InitialConfig(
                authToken,
                estabCode,
                routeCode,
                driverCode,
                sensorCode
            )
        )
        onFinished.invoke()
    }
}