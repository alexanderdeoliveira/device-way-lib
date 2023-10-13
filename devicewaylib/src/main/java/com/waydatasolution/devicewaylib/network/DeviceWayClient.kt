package com.waydatasolution.devicewaylib.network

import com.waydatasolution.devicewaylib.data.model.SendDataRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

internal interface DeviceWayClient {

    @PUT("api/v1/bluetooth/medicoes")
    suspend fun sendData(
        @Body request: SendDataRequest
    ): Response<Unit>
}