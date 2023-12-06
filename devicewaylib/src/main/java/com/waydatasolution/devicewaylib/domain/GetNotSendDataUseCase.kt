package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.NotSendData

internal interface GetNotSendDataUseCase {
    suspend operator fun invoke(): List<NotSendData>
}