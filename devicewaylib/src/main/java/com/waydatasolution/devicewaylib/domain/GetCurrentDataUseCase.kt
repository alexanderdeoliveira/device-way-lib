package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.Data

internal interface GetCurrentDataUseCase {
    suspend operator fun invoke(
        sensorId: String
    ): List<Data>?
}