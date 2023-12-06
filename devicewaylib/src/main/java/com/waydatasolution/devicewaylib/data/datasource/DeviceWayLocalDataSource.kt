package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.NotSendData
import com.waydatasolution.devicewaylib.data.model.QueryParam

internal interface DeviceWayLocalDataSource {
    suspend fun saveData(
        device: BluetoothDevice,
        onFinished: () -> Unit
    )

    suspend fun getCurrentData(
        sensorId: String
    ): List<Data>?

    suspend fun getNotSendData(): List<NotSendData>

    suspend fun getAllDevicesMac(): List<String>

    suspend fun getDataBlockByMac(mac: String): List<Data>

    suspend fun deleteUntil(data: Data)

    suspend fun saveQueryParams(
        queryParams: List<QueryParam>
    )

    suspend fun getQueryParams(): List<QueryParam>
    suspend fun getAuthToken(): String
    suspend fun clearDatabase()

}