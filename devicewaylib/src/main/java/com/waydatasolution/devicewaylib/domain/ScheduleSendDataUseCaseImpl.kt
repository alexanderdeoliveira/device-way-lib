package com.waydatasolution.devicewaylib.domain

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.waydatasolution.devicewaylib.util.SEND_DATA_WORKER_NAME

internal class ScheduleSendDataUseCaseImpl(
    private val workManager: WorkManager,
    private val sendDataWorkRequest: OneTimeWorkRequest
): ScheduleSendDataUseCase {
    override fun invoke() {
        workManager.enqueueUniqueWork(SEND_DATA_WORKER_NAME, ExistingWorkPolicy.REPLACE, sendDataWorkRequest)
    }
}