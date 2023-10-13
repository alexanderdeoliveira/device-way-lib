package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import retrofit2.Response

internal interface DeviceWayRemoteDataSource {
    suspend fun sendData(request: SendDataRequest): Response<Unit>
}