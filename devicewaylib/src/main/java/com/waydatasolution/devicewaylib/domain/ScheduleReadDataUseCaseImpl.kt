package com.waydatasolution.devicewaylib.domain

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.waydatasolution.devicewaylib.util.READ_DATA_WORKER_NAME

internal class ScheduleReadDataUseCaseImpl(
    private val workManager: WorkManager,
    private val readDataWorkRequest: OneTimeWorkRequest
): ScheduleReadDataUseCase {
    override fun invoke() {
        workManager.enqueueUniqueWork(READ_DATA_WORKER_NAME, ExistingWorkPolicy.KEEP, readDataWorkRequest)
    }
}