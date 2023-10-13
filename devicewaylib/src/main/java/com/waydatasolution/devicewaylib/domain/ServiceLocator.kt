package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.network.DeviceWayNetworkModule
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayLocalDataSource
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayRemoteDataSource
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal interface ServiceLocator {
    val networkModule: DeviceWayNetworkModule
    val remoteDataSource: DeviceWayRemoteDataSource
    val localDataSource: DeviceWayLocalDataSource
    val repository: DeviceWayRepository
    val saveDataUseCase: SaveDataUseCase
    val readDataUseCase: ReadDataUseCase
    val sendDataUseCase: SendDataUseCase
    val scheduleSendDataUseCase: ScheduleSendDataUseCase
}