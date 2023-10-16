package com.waydatasolution.devicewaylib

import android.content.Context
import android.content.Intent
import com.waydatasolution.devicewaylib.domain.ServiceLocator
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DeviceWayLib {

    private lateinit var serviceLocator: ServiceLocator

    fun init(
        context: Context,
        authToken: String,
        estabCode: Int,
        routeCode: Int,
        driverCode: Int,
        sensorCode: Int
    ) {
        serviceLocator = ServiceLocatorImpl.getInstance(context)
        saveInitialConfig(
            authToken,
            estabCode,
            routeCode,
            driverCode,
            sensorCode
        ) {
            context.startService(
                Intent(
                    context,
                    DeviceWayService::class.java
                )
            )
        }
    }

    private fun saveInitialConfig(
        authToken: String,
        estabCode: Int,
        routeCode: Int,
        driverCode: Int,
        sensorCode: Int,
        onFinished: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            serviceLocator.saveInitialConfigUseCase(
                authToken,
                estabCode,
                routeCode,
                driverCode,
                sensorCode,
                onFinished
            )
        }
    }
}