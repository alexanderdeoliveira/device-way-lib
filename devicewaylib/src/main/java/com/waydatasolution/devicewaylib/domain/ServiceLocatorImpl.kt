package com.waydatasolution.devicewaylib.domain

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.waydatasolution.devicewaylib.worker.SendDataWorker
import com.waydatasolution.devicewaylib.network.DeviceWayNetworkModule
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayLocalDataSource
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayLocalDataSourceImpl
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayRemoteDataSource
import com.waydatasolution.devicewaylib.data.datasource.DeviceWayRemoteDataSourceImpl
import com.waydatasolution.devicewaylib.data.db.DeviceWayLibDb
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepositoryImpl
import com.waydatasolution.devicewaylib.util.SEND_DATA_DELAY_TIMER_IN_MINUTES
import com.waydatasolution.devicewaylib.worker.ReadDataWorker
import java.util.concurrent.TimeUnit

internal class ServiceLocatorImpl(
    private val context: Context,
    private val isDebug: Boolean
): ServiceLocator {
    override val networkModule: DeviceWayNetworkModule by lazy {
        DeviceWayNetworkModule(context, isDebug)
    }

    override val remoteDataSource: DeviceWayRemoteDataSource by lazy {
        DeviceWayRemoteDataSourceImpl(
            networkModule.getClient()
        )
    }

    override val localDataSource: DeviceWayLocalDataSource by lazy {
        DeviceWayLocalDataSourceImpl(
            DeviceWayLibDb.getDatabase(context).missionDao(),
            DeviceWayLibDb.getDatabase(context).dataDao(),
            DeviceWayLibDb.getDatabase(context).queryParamDao()
        )
    }
    override val repository: DeviceWayRepository by lazy { DeviceWayRepositoryImpl(localDataSource, remoteDataSource) }

    override val saveDataUseCase by lazy { SaveDataUseCaseImpl(repository) }
    override val readDataUseCase by lazy { ReadDataUseCaseImpl() }
    override val sendDataUseCase by lazy { SendDataUseCaseImpl(repository) }
    override val scheduleSendDataUseCase by lazy { ScheduleSendDataUseCaseImpl(
        WorkManager.getInstance(context.applicationContext),
        OneTimeWorkRequestBuilder<SendDataWorker>()
            .setInitialDelay(SEND_DATA_DELAY_TIMER_IN_MINUTES, TimeUnit.SECONDS)
            .build()
    )}
    override val scheduleReadDataUseCase by lazy { ScheduleReadDataUseCaseImpl(
        WorkManager.getInstance(context.applicationContext),
        OneTimeWorkRequestBuilder<ReadDataWorker>().build()
    )}

    override val saveQueryParamsUseCase by lazy { SaveQueryParamsUseCaseImpl(repository) }
    override val getCurrentDataUseCase by lazy { GetCurrentDataUseCaseImpl(repository) }
    override val getNotSendDataUseCase by lazy { GetNotSendDataUseCaseImpl(repository) }
    override val clearDatabaseUseCase by lazy { ClearDatabaseUseCaseImpl(repository) }

    companion object {

        private var instance: ServiceLocatorImpl? = null

        fun getInstance(
            context: Context,
            isDebug: Boolean
        ) = instance ?: ServiceLocatorImpl(context, isDebug).also { instance = it }
    }
}