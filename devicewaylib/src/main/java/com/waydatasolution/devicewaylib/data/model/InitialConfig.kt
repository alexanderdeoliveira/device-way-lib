package com.waydatasolution.devicewaylib.data.model

internal data class InitialConfig(
    val authToken: String,
    val estabCode: Int,
    val routeCode: Int,
    val driverCode: Int,
    val sensorCode: Int
)