package com.waydatasolution.devicewaylib.data.repository

import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.NotSendData
import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.data.model.ResponseStatus

internal interface DeviceWayRepository {
    suspend fun saveData(
        device: BluetoothDevice,
        onFinished: () -> Unit
    )
    suspend fun sendData(): ResponseStatus
    suspend fun saveQueryParams(
        queryParams: List<QueryParam>
    )
    suspend fun getQueryParams(): List<QueryParam>
    suspend fun getAuthToken(): String
    suspend fun getCurrentData(
        sensorId: String
    ): List<Data>?

    suspend fun getNotSendData(): List<NotSendData>
    suspend fun clearDatabase()
}