package com.waydatasolution.devicewaylib.domain

internal interface SendDataUseCase {
    suspend operator fun invoke(): Boolean
}