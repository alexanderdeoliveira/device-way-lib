package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository
import java.lang.Exception

internal class SendDataUseCaseImpl(
    private val repository: DeviceWayRepository
): SendDataUseCase {
    override suspend fun invoke(): Boolean {
        return try {
//            repository.sendData()
            true
        } catch (e: Exception) {
            false
        }
    }
}