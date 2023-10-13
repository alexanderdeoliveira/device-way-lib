package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.Sample

internal interface SaveDataUseCase {
    suspend operator fun invoke(
        sensorId: String,
        samples: List<Sample>,
        onFinished: () -> Unit
    )
}