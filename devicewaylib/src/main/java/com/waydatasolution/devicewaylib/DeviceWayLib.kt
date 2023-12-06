package com.waydatasolution.devicewaylib

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.waydatasolution.devicewaylib.data.model.Data
import com.waydatasolution.devicewaylib.domain.ServiceLocator
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DeviceWayLib {

    private lateinit var serviceLocator: ServiceLocator
    private var isDebug: Boolean = true

    fun init(
        context: Context,
        isDebug: Boolean,
        queryParams: Map<String, String>
    ) {
        this.isDebug = isDebug
        saveInitialConfig(
            context,
            queryParams
        )
    }

    fun start(
        context: Context,
        sensorIdList: ArrayList<String>,
        timer: Long
    ) {
        val intent = Intent(
            context,
            DeviceWayService::class.java
        )
        intent.putStringArrayListExtra(DeviceWayService.SENSOR_ID_LIST_EXTRA, sensorIdList)
        intent.putExtra(DeviceWayService.TIMER_EXTRA, timer)
        context.startService(intent)

//        val intent = Intent(
//            context,
//            DeviceWayService2::class.java
//        )
//        intent.putStringArrayListExtra(DeviceWayService.SENSOR_ID_LIST_EXTRA, sensorIdList)
//        intent.putExtra(DeviceWayService.TIMER_EXTRA, timer)
//        context.startService(intent)
    }

    fun finish(
        context: Context
    ) {
        stopService(context)
        clearDatabase(context)
    }

    fun getNotSendData(
        context: Context,
        callback: (String) -> Unit
    ) {
        serviceLocator = ServiceLocatorImpl.getInstance(
            context,
            isDebug
        )
        CoroutineScope(Dispatchers.IO).launch {
            val noSendDataList = serviceLocator.getNotSendDataUseCase()
            callback.invoke(Gson().toJson(Gson().toJson(noSendDataList)))
        }
    }

    fun getNotSendDataCount(
        context: Context,
        callback: (Int) -> Unit
    ) {
        serviceLocator = ServiceLocatorImpl.getInstance(context, isDebug)
        CoroutineScope(Dispatchers.IO).launch {
            val notSendDataList = serviceLocator.getNotSendDataUseCase()
            var size = 0
            notSendDataList.map {
                size += it.dataList.size
            }
            callback.invoke(size)
        }
    }

    private fun clearDatabase(
        context: Context
    ) {
        serviceLocator = ServiceLocatorImpl.getInstance(context, isDebug)
        CoroutineScope(Dispatchers.IO).launch {
            serviceLocator.clearDatabaseUseCase()
        }
    }

    private fun stopService(
        context: Context
    ) {
        val intent = Intent(
            context,
            DeviceWayService::class.java
        )
        context.stopService(intent)
    }

    private fun saveInitialConfig(
        context: Context,
        queryParams: Map<String, String>
    ) {
        serviceLocator = ServiceLocatorImpl.getInstance(context, isDebug)
        CoroutineScope(Dispatchers.IO).launch {
            serviceLocator.saveQueryParamsUseCase(queryParams)
        }
    }

    fun getCurrentData(
        context: Context,
        sensorId: String,
        delayInMillis: Long,
        onResult: (List<Data>?) -> Unit
    ) {
        serviceLocator = ServiceLocatorImpl.getInstance(context, isDebug)
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val lastData = serviceLocator.getCurrentDataUseCase(sensorId)
                onResult.invoke(lastData)
                delay(delayInMillis)
            }
        }
    }

    fun startLiveScan(
        context: Context,
        sensorIdList: ArrayList<String>,
        callbackStatus: (String) -> Unit
    ) {
        stopService(context)
        DeviceWayLive(
            context,
            isDebug,
            sensorIdList,
            callbackStatus
        ).start()
    }

    enum class DeviceWayStatus(val statusText: String) {
        STANDBY("Stand-by"),
        SEARCHING("Buscando..."),
        READING("Fazendo leituras..."),
        SAVING("Salvando..."),
        SENDING("Enviando..."),
        SEND_DATA_SUCCESS("Dados enviados com sucesso"),
        SEND_DATA_FAILED("Erro ao enviar dados"),
        NOT_FOUND("Dispositivo não encontrado"),
        NO_DATA("Dispositivo sem leituras"),
        BLUETOOTH_DISABLED("Bluetooth desabilitado")
    }
}