package com.waydatasolution.devicewaylib.data.datasource

import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.data.model.ResponseStatus
import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import retrofit2.Response

internal interface DeviceWayRemoteDataSource {
    suspend fun sendData(
        queryParams: List<QueryParam>,
        request: SendDataRequest
    ): ResponseStatus
}