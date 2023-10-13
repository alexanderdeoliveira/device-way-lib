package com.waydatasolution.devicewaylib.data.repository

import com.waydatasolution.devicewaylib.data.datasource.DeviceWayLocalDataSource
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayRemoteDataSource
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.InitialConfig
import com.waydatasolution.devicewaylib.data.model.Sample
import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import com.waydatasolution.devicewaylib.data.model.toDataRequest

internal class DeviceWayRepositoryImpl(
    private val deviceWayLocalDataSource: DeviceWayLocalDataSource,
    private val deviceWayRemoteDataSource: DeviceWayRemoteDataSource
): DeviceWayRepository {
    override suspend fun saveData(
        sensorId: String,
        samples: List<Sample>,
        onFinished: () -> Unit
    ) {
        deviceWayLocalDataSource.saveData(
            sensorId,
            samples,
            onFinished
        )
    }

    override suspend fun getInitialConfig(): InitialConfig {
        return deviceWayLocalDataSource.getInitialConfig()
    }

    override suspend fun sendData() {
        val devicesMacList = deviceWayLocalDataSource.getAllDevicesMac()
        devicesMacList.map {
            var dataList = deviceWayLocalDataSource.getDataBlockByMac(it)
            while (dataList.isNotEmpty()) {
                dataList = if (sendDataPerBlock(dataList)) {
                    deviceWayLocalDataSource.getDataBlockByMac(it)
                } else {
                    listOf()
                }
            }
        }
    }

    private suspend fun sendDataPerBlock(dataList: List<Data>): Boolean {
        val response = deviceWayRemoteDataSource.sendData(
            SendDataRequest(dataList.toDataRequest())
        )

        return if (response.isSuccessful) {
            deviceWayLocalDataSource.deleteUntil(dataList.last())
            true
        } else {
            false
        }
    }
}