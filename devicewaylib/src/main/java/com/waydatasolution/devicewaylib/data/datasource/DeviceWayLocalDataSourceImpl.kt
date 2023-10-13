package com.waydatasolution.devicewaylib.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.waydatasolution.devicewaylib.data.dao.DataDao
import com.waydatasolution.devicewaylib.data.dao.MissionDao
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.data.model.InitialConfig
import com.waydatasolution.devicewaylib.data.model.Mission
import com.waydatasolution.devicewaylib.data.model.Sample
import com.waydatasolution.devicewaylib.data.model.toDataModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class DeviceWayLocalDataSourceImpl(
    private val context: Context,
    private val missionDao: MissionDao,
    private val dataDao: DataDao
): DeviceWayLocalDataSource {

    override suspend fun saveData(
        sensorId: String,
        samples: List<Sample>,
        onFinished: () -> Unit
    ) {
        val missionList = missionDao.getLastMissionOfId(sensorId)
        if (missionList.isNotEmpty()) {
            val currentMission = missionList[0]
            if (currentMission.timestamp != samples[0].timestamp.time || currentMission.samplesCount != samples.size) {
                save(sensorId, samples)
            }
        } else {
            val mission = Mission(sensorId, samples[0].timestamp.time, samples.size)
            missionDao.insert(mission)
            save(sensorId, samples)
        }

        onFinished.invoke()
    }

    override suspend fun getAllDevicesMac(): List<String> {
        return dataDao.getAllMacs()
    }

    override suspend fun getDataBlockByMac(mac: String): List<Data> {
        return dataDao.getAll(mac)
    }

    override suspend fun deleteUntil(data: Data) {
        dataDao.deleteUntil(data.id)
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

    override suspend fun saveConfig(initialConfig: InitialConfig) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN_KEY] = initialConfig.authToken
            prefs[ESTAB_CODE_KEY] = initialConfig.estabCode
            prefs[ROUTE_CODE_KEY] = initialConfig.routeCode
            prefs[DRIVER_CODE_KEY] = initialConfig.driverCode
            prefs[SENSOR_CODE_KEY] = initialConfig.sensorCode
        }
    }

    override suspend fun getInitialConfig(): InitialConfig {
        val authToken = context.dataStore.data.map { prefs ->
            prefs[AUTH_TOKEN_KEY] ?: ""
        }.first()

        val estabCode = context.dataStore.data.map { prefs ->
            prefs[ESTAB_CODE_KEY] ?: 0
        }.first()

        val routeCode = context.dataStore.data.map { prefs ->
            prefs[ROUTE_CODE_KEY] ?: 0
        }.first()

        val driverCode = context.dataStore.data.map { prefs ->
            prefs[DRIVER_CODE_KEY] ?: 0
        }.first()

        val sensorCode = context.dataStore.data.map { prefs ->
            prefs[SENSOR_CODE_KEY] ?: 0
        }.first()

        return InitialConfig(
            authToken = authToken,
            estabCode = estabCode,
            routeCode = routeCode,
            driverCode = driverCode,
            sensorCode = sensorCode
        )
    }

    companion object {
        private const val authTokenKey = "authTokenKey"
        private const val estabCodeKey = "estabCodeKey"
        private const val routeCodeKey = "routeCodeKey"
        private const val driverCodeKey = "driverCodeKey"
        private const val sensorCodeKey = "sensorCodeKey"

        val AUTH_TOKEN_KEY = stringPreferencesKey(authTokenKey)
        val ESTAB_CODE_KEY = intPreferencesKey(estabCodeKey)
        val ROUTE_CODE_KEY = intPreferencesKey(routeCodeKey)
        val DRIVER_CODE_KEY = intPreferencesKey(driverCodeKey)
        val SENSOR_CODE_KEY = intPreferencesKey(sensorCodeKey)

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.deviceway.shared.preferences")
    }
}