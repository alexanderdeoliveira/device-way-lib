package com.waydatasolution.devicewaylib.network

import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.QueryMap

internal interface DeviceWayClient {

    @PUT("api/v1/bluetoothway/medicoes")
    suspend fun sendData(
        @QueryMap queryParams: Map<String, String>,
        @Body request: SendDataRequest
    ): Response<Unit>
}