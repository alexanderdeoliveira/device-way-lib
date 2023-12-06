package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.data.dao.DataDao
import com.waydatasolution.devicewaylib.data.dao.MissionDao
import com.waydatasolution.devicewaylib.data.dao.QueryParamDao
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.NotSendData
import com.waydatasolution.devicewaylib.data.model.Mission
import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.data.model.Sample
import com.waydatasolution.devicewaylib.data.model.toDataModel
import java.util.Date

internal class DeviceWayLocalDataSourceImpl(
    private val missionDao: MissionDao,
    private val dataDao: DataDao,
    private val queryParamDao: QueryParamDao
): DeviceWayLocalDataSource {

    override suspend fun saveData(
        device: BluetoothDevice,
        onFinished: () -> Unit
    ) {
        var mission = missionDao.getMissionById(device.SN)
        if (mission != null) {
            if (mission.timestamp != device.samples[0].timestamp.time || mission.samplesCount != device.samples.size) {
                save(device.SN, device.samples)
            }
        } else {
            mission = Mission(device.SN, device.samples[0].timestamp.time, device.samples.size)
            missionDao.insert(mission)
            save(device.SN, device.samples)
        }

        saveCurrentSample(device)

        onFinished.invoke()
    }

    override suspend fun getCurrentData(
        sensorId: String
    ): List<Data>? {
        return dataDao.getCurrentData(sensorId)
    }

    override suspend fun getNotSendData(): List<NotSendData> {
        val notSendDataList = mutableListOf<NotSendData>()
        val devices = dataDao.getAllMacs()
        devices.map { mac ->
            val dataList = dataDao.getAllByMac(mac)
            notSendDataList.add(
                NotSendData(
                    mac = mac,
                    dataList = dataList
                )
            )
        }

        return notSendDataList
    }

    private suspend fun saveCurrentSample(device: BluetoothDevice) {
        val currentSample = Sample(
            position = 0,
            timestamp = Date(),
            temperature = device.Temperature,
            humidity = device.Humidity
        )

        save(device.SN, listOf(currentSample))
    }

    override suspend fun getAllDevicesMac(): List<String> {
        return dataDao.getAllMacs()
    }

    override suspend fun getDataBlockByMac(mac: String): List<Data> {
        return dataDao.getDataBlockByMac(mac)
    }

    override suspend fun deleteUntil(data: Data) {
        dataDao.deleteUntil(data.date)
    }

    override suspend fun saveQueryParams(queryParams: List<QueryParam>) {
        queryParamDao.insert(queryParams)
    }

    override suspend fun getQueryParams(): List<QueryParam> {
        return queryParamDao.getAll()
    }

    override suspend fun getAuthToken(): String {
        return queryParamDao.getAuthToken()
    }

    private suspend fun save(
        sensorId: String,
        samples: List<Sample>
    ) {
        val dataList = mutableListOf<Data>()
        samples.map {
            dataList.addAll(it.toDataModel(sensorId))
        }

        dataDao.insert(dataList)
    }

    override suspend fun clearDatabase() {
        dataDao.deleteAll()
        missionDao.deleteAll()
        queryParamDao.deleteAll()
    }
}