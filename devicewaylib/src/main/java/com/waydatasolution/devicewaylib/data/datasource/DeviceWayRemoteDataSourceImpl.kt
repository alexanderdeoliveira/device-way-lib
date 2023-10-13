package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.network.DeviceWayClient
import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import retrofit2.Response

internal class DeviceWayRemoteDataSourceImpl(
    private val client: DeviceWayClient
): DeviceWayRemoteDataSource {

    override suspend fun sendData(request: SendDataRequest): Response<Unit> {
        return client.sendData(request)
    }
}