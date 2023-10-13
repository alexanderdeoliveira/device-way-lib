package com.waydatasolution.devicewaylib

import android.content.Context
import android.content.Intent
import com.waydatasolution.devicewaylib.domain.ServiceLocator

object DeviceWayLib {

    private lateinit var serviceLocator: ServiceLocator

    fun init(context: Context) {
        context.startService(Intent(context, DeviceWayService::class.java))
    }
}