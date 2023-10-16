package com.waydatasolution.devicewaylib.domain

internal interface SaveInitialConfigUseCase {
    suspend operator fun invoke(
        authToken: String,
        estabCode: Int,
        routeCode: Int,
        driverCode: Int,
        sensorCode: Int,
        onFinished: () -> Unit
    )
}