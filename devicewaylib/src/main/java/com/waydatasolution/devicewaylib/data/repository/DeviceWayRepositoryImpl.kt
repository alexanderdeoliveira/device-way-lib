package com.waydatasolution.devicewaylib.data.repository

import com.waydatasolution.devicewaylib.data.datasource.DeviceWayLocalDataSource
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayRemoteDataSource
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.NotSendData
import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.data.model.ResponseStatus
import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import com.waydatasolution.devicewaylib.data.model.toDataRequest

internal class DeviceWayRepositoryImpl(
    private val deviceWayLocalDataSource: DeviceWayLocalDataSource,
    private val deviceWayRemoteDataSource: DeviceWayRemoteDataSource
): DeviceWayRepository {
    override suspend fun saveData(
        device: BluetoothDevice,
        onFinished: () -> Unit
    ) {
        deviceWayLocalDataSource.saveData(
            device,
            onFinished
        )
    }

    override suspend fun getCurrentData(
        sensorId: String
    ): List<Data>? {
        return deviceWayLocalDataSource.getCurrentData(sensorId)
    }

    override suspend fun getNotSendData(): List<NotSendData> {
        return deviceWayLocalDataSource.getNotSendData()
    }

    override suspend fun clearDatabase() {
        deviceWayLocalDataSource.clearDatabase()
    }

    override suspend fun sendData(): ResponseStatus {
        val devicesMacList = deviceWayLocalDataSource.getAllDevicesMac()
        var lastResponse: ResponseStatus = ResponseStatus.Success(200)
        devicesMacList.map {
            var dataList = deviceWayLocalDataSource.getDataBlockByMac(it)
            while (dataList.isNotEmpty()) {
                lastResponse = sendDataPerBlock(dataList)
                if (lastResponse is ResponseStatus.Success) {
                    dataList = deviceWayLocalDataSource.getDataBlockByMac(it)
                } else {
                    return@map
                }
            }
        }

        return lastResponse
    }

    override suspend fun saveQueryParams(queryParams: List<QueryParam>) {
        deviceWayLocalDataSource.saveQueryParams(queryParams)
    }

    override suspend fun getQueryParams(): List<QueryParam> {
        return deviceWayLocalDataSource.getQueryParams()
    }

    override suspend fun getAuthToken(): String {
        return deviceWayLocalDataSource.getAuthToken()
    }

    private suspend fun sendDataPerBlock(dataList: List<Data>): ResponseStatus {
        val response = deviceWayRemoteDataSource.sendData(
            deviceWayLocalDataSource.getQueryParams(),
            SendDataRequest(dataList.toDataRequest())
        )

        if (response is ResponseStatus.Success) {
            deviceWayLocalDataSource.deleteUntil(dataList.last())
        }

        return response
    }
}