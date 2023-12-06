package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.ResponseStatus

internal interface SendDataUseCase {
    suspend operator fun invoke(): ResponseStatus
}