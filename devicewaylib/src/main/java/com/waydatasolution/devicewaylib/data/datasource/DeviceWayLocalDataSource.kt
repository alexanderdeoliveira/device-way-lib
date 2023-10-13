package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.InitialConfig
import com.waydatasolution.devicewaylib.data.model.Sample

internal interface DeviceWayLocalDataSource {
    suspend fun saveData(
        sensorId: String,
        samples: List<Sample>,
        onFinished: () -> Unit
    )

    suspend fun getAllDevicesMac(): List<String>

    suspend fun getDataBlockByMac(mac: String): List<Data>

    suspend fun deleteUntil(data: Data)

    suspend fun saveConfig(
        initialConfig: InitialConfig
    )

    suspend fun getInitialConfig(): InitialConfig

}