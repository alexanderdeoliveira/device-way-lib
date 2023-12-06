package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.data.model.ResponseStatus
import com.waydatasolution.devicewaylib.network.DeviceWayClient
import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import com.waydatasolution.devicewaylib.data.model.parseResponse
import com.waydatasolution.devicewaylib.util.AUTH_TOKEN
import retrofit2.Response

internal class DeviceWayRemoteDataSourceImpl(
    private val client: DeviceWayClient
): DeviceWayRemoteDataSource {

    override suspend fun sendData(
        queryParams: List<QueryParam>,
        request: SendDataRequest
    ): ResponseStatus {
        val queryMap = mutableMapOf<String, String>()
        queryParams.map {
        if (it.key != AUTH_TOKEN)
            queryMap[it.key] = it.value
        }

        val response = client.sendData(
            queryMap,
            request
        )

        return response.parseResponse()
    }
}