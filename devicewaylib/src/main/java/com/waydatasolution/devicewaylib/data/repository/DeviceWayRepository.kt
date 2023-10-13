package com.waydatasolution.devicewaylib.data.repository

import com.waydatasolution.devicewaylib.data.model.InitialConfig
import com.waydatasolution.devicewaylib.data.model.Sample

internal interface DeviceWayRepository {
    suspend fun saveData(
        sensorId: String,
        samples: List<Sample>,
        onFinished: () -> Unit
    )
    suspend fun sendData()
    suspend fun getInitialConfig(): InitialConfig
}